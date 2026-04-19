package hu.beni.amusementpark.test.unit;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.service.impl.VisitorServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VisitorServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;
    private AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;
    private PasswordEncoder passwordEncoder;

    private VisitorService visitorService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        amusementParkKnowVisitorRepository = mock(AmusementParkKnowVisitorRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        visitorService = new VisitorServiceImpl(amusementParkRepository, machineRepository, visitorRepository,
                amusementParkKnowVisitorRepository, passwordEncoder);
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository,
                amusementParkKnowVisitorRepository);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInNegativeNoVisitorWithUsername() {
        String email = "nembence1994@gmail.com";

        assertThatThrownBy(() -> visitorService.findByEmailMakeFreshlyLoggedIn(email))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, email));

        verify(visitorRepository).findById(email);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInPositive() {
        Visitor visitorRequest = Visitor.builder().email("nembence1994@gmail.com").build();
        String email = visitorRequest.getEmail();
        Visitor visitorInDb = Visitor.builder().email(email)
                .amusementPark(AmusementPark.builder().id(0L).build())
                .machine(Machine.builder().build()).build();
        ;
        when(visitorRepository.findById(email)).thenReturn(Optional.of(visitorInDb));

        Visitor visitorResponse = visitorService.findByEmailMakeFreshlyLoggedIn(email);

        assertNull(visitorResponse.getAmusementPark());
        assertNull(visitorResponse.getMachine());
        verify(visitorRepository).findById(email);
    }

    @Test
    public void signUpNegativeEmailAlreadyTaken() {
        Visitor visitor = Visitor.builder().email("nembence1994@gmail.com").build();
        when(visitorRepository.countByEmail(visitor.getEmail())).thenReturn(1L);

        assertThatThrownBy(() -> visitorService.signUp(visitor)).isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(EMAIL_ALREADY_TAKEN, visitor.getEmail()));

        verify(visitorRepository).countByEmail(visitor.getEmail());
    }

    @Test
    public void signUpPositive() {
        Visitor visitor = Visitor.builder().email("nembence1994@gmail.com").password("Pass1234").build();
        when(visitorRepository.save(visitor)).thenReturn(visitor);

        assertEquals(visitor, visitorService.signUp(visitor));

        assertNotNull(visitor.getSpendingMoney());
        assertNotNull(visitor.getAuthority());
        verify(visitorRepository).countByEmail(visitor.getEmail());
        verify(visitorRepository).save(visitor);
    }

    @Test
    public void enterParkNegativeNoPark() {
        Long amusementParkId = 0L;
        String visitorEmail = "bence@gmail.com";

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
    }

    @Test
    public void enterParkNegativeNotSignedUp() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        String visitorEmail = "bence@gmail.com";
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
                .thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
    }

    @Test
    public void enterParkNegativeNotEnoughMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(20).build();
        String visitorEmail = visitor.getEmail();
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
    }

    @Test
    public void enterParkNegativeVisitorIsInAPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(100).amusementPark(amusementPark).build();
        String visitorEmail = visitor.getEmail();
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_IN_A_PARK);

        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
    }

    @Test
    public void enterParkPositiveAddVisitorToKnown() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(100).build();
        String visitorEmail = visitor.getEmail();
        Integer spendingMoney = visitor.getSpendingMoney();
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

        visitorService.enterPark(amusementParkId, visitorEmail);

        assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
        verify(amusementParkKnowVisitorRepository).save(any());
        verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
    }

    @Test
    public void enterParkPositiveVisitorAlreadyKnown() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(100).build();
        String visitorEmail = visitor.getEmail();
        Integer spendingMoney = visitor.getSpendingMoney();
        when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));
        when(amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(1L);

        visitorService.enterPark(amusementParkId, visitorEmail);

        assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
        verify(visitorRepository).findById(visitorEmail);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
        verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
    }

    @Test
    public void getOnMachineNegativeNoMachineInPark() {
        Long amusementParkId = 0L;
        Long machineId = 1L;
        String visitorEmail = "bence@gmail.com";

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void getOnMachineNegativeNoVisitorInPark() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        String visitorEmail = "bence@gmail.com";
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }

    @Test
    public void getOnMachineNegativeOnMachine() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").machine(machine).build();
        String visitorEmail = visitor.getEmail();
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_ON_A_MACHINE);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }

    @Test
    public void getOnMachineNegativeNotEnoughtMoney() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(40).build();
        String visitorEmail = visitor.getEmail();
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }

    @Test
    public void getOnMachineNegativeTooYoung() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(40).dateOfBirth(LocalDate.now()).build();
        String visitorEmail = visitor.getEmail();
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_TOO_YOUNG);

        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }

    @Test
    public void getOnMachinePositive() {
        Long amusementParkId = 0L;
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email("bence@gmail.com").spendingMoney(40)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        String visitorEmail = visitor.getEmail();
        Integer spendingMoney = visitor.getSpendingMoney();
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        visitorService.getOnMachine(amusementParkId, machineId, visitorEmail);

        assertEquals(spendingMoney - machine.getTicketPrice(), visitor.getSpendingMoney());
        assertEquals(machine, visitor.getMachine());
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
        verify(amusementParkRepository).incrementCapitalById(machine.getTicketPrice(), amusementParkId);
    }

    @Test
    public void getOffMachineNegativeNoVisitor() {
        Long amusementParkId = 10L;
        Long machineId = 0L;
        String visitorEmail = "bence@gmail.com";

        assertThatThrownBy(() -> visitorService.getOffMachine(amusementParkId, machineId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_ON_MACHINE_WITH_ID);

        verify(visitorRepository).findByMachineIdAndVisitorEmail(machineId, visitorEmail);
    }

    @Test
    public void getOffMachinePositive() {
        Long amusementParkId = 10L;
        Long machineId = 0L;
        Visitor visitor = Visitor.builder().email("bence@gmail.com").machine(Machine.builder().id(machineId).build()).build();
        String visitorEmail = visitor.getEmail();
        when(visitorRepository.findByMachineIdAndVisitorEmail(machineId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        visitorService.getOffMachine(amusementParkId, machineId, visitorEmail);

        assertNull(visitor.getMachine());
        verify(visitorRepository).findByMachineIdAndVisitorEmail(machineId, visitorEmail);
    }

    @Test
    public void leaveParkNegativeNoVisitorInPark() {
        Long amusementParkId = 0L;
        String visitorEmail = "bence@gmail.com";

        assertThatThrownBy(() -> visitorService.leavePark(amusementParkId, visitorEmail))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }

    @Test
    public void leaveParkPositive() {
        Long amusementParkId = 0L;
        Visitor visitor = Visitor.builder().email("bence@gmail.com").amusementPark(AmusementPark.builder().build())
                .spendingMoney(100).build();
        String visitorEmail = visitor.getEmail();
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
                .thenReturn(Optional.of(visitor));

        visitorService.leavePark(amusementParkId, visitorEmail);

        assertNull(visitor.getAmusementPark());
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
    }
}