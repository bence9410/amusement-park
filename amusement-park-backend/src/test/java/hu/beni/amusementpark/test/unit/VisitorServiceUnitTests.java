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

import java.time.LocalDate;
import java.util.Optional;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.EMAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VisitorServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private VisitorRepository visitorRepository;
    private AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;

    private VisitorService visitorService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        visitorRepository = mock(VisitorRepository.class);
        amusementParkKnowVisitorRepository = mock(AmusementParkKnowVisitorRepository.class);
        visitorService = new VisitorServiceImpl(amusementParkRepository, machineRepository, visitorRepository,
                amusementParkKnowVisitorRepository, new BCryptPasswordEncoder());
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository,
                amusementParkKnowVisitorRepository);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInNegativeNoVisitorWithUsername() {
        assertThatThrownBy(() -> visitorService.findByEmailMakeFreshlyLoggedIn(EMAIL))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, EMAIL));

        verify(visitorRepository).findById(EMAIL);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInPositive() {
        Visitor visitorRequest = Visitor.builder().email(EMAIL).build();
        Visitor visitorInDb = Visitor.builder().email(EMAIL)
                .amusementPark(AmusementPark.builder().id(0L).build())
                .machine(Machine.builder().build()).build();
        ;
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitorInDb));

        Visitor visitorResponse = visitorService.findByEmailMakeFreshlyLoggedIn(EMAIL);

        assertNull(visitorResponse.getAmusementPark());
        assertNull(visitorResponse.getMachine());
        verify(visitorRepository).findById(EMAIL);
    }

    @Test
    public void signUpNegativeEmailAlreadyTaken() {
        Visitor visitor = Visitor.builder().email(EMAIL).build();
        when(visitorRepository.countByEmail(visitor.getEmail())).thenReturn(1L);

        assertThatThrownBy(() -> visitorService.signUp(visitor)).isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(EMAIL_ALREADY_TAKEN, visitor.getEmail()));

        verify(visitorRepository).countByEmail(visitor.getEmail());
    }

    @Test
    public void signUpPositive() {
        Visitor visitor = Visitor.builder().email(EMAIL).password("Pass1234").build();
        when(visitorRepository.save(visitor)).thenReturn(visitor);

        assertEquals(visitor, visitorService.signUp(visitor));

        assertNotNull(visitor.getMoney());
        assertNotNull(visitor.getAuthority());
        verify(visitorRepository).countByEmail(visitor.getEmail());
        verify(visitorRepository).save(visitor);
    }

    @Test
    public void enterParkNegativeNoPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void enterParkNegativeNotSignedUp() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
    }

    @Test
    public void enterParkNegativeNotEnoughMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(20).coupon(20).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
    }

    @Test
    public void enterParkNegativeVisitorIsInAPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(100).coupon(0).amusementPark(amusementPark).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_IN_A_PARK);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
    }

    @Test
    public void enterParkPositiveAddVisitorToKnown() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));

        visitorService.enterPark(amusementParkId, EMAIL);

        assertEquals(50, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(60, ownerVisitor.getMoney());
        assertEquals(10, ownerVisitor.getCoupon());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
        verify(amusementParkKnowVisitorRepository).save(any());
    }

    @Test
    public void enterParkPositiveVisitorAlreadyKnown() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));
        when(amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        visitorService.enterPark(amusementParkId, EMAIL);

        assertEquals(50, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(60, ownerVisitor.getMoney());
        assertEquals(10, ownerVisitor.getCoupon());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void enterParkPositivePayWithCoupon() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).coupon(50).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));
        when(amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        visitorService.enterPark(amusementParkId, EMAIL);

        assertEquals(100, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(10, ownerVisitor.getMoney());
        assertEquals(60, ownerVisitor.getCoupon());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void enterParkPositivePayWithLeftOverCoupon() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).coupon(40).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(visitorRepository.findById(EMAIL)).thenReturn(Optional.of(visitor));
        when(amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        visitorService.enterPark(amusementParkId, EMAIL);

        assertEquals(90, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(20, ownerVisitor.getMoney());
        assertEquals(50, ownerVisitor.getCoupon());
        assertEquals(amusementPark, visitor.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(visitorRepository).findById(EMAIL);
        verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void getOnMachineNegativeNoMachineInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Long machineId = 1L;
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void getOnMachineNegativeNoVisitorInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeOnMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).machine(machine).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(AmusementPark.builder().id(amusementParkId).build()));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_ON_A_MACHINE);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeNotEnoughtMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(30).coupon(10).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeTooYoung() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(10).coupon(10).dateOfBirth(LocalDate.now()).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_TOO_YOUNG);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositive() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(40).coupon(0)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        visitorService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(20, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(30, ownerVisitor.getMoney());
        assertEquals(10, ownerVisitor.getCoupon());
        assertEquals(machine, visitor.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositivePayWithCoupon() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(40).coupon(20)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        visitorService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(40, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(10, ownerVisitor.getMoney());
        assertEquals(30, ownerVisitor.getCoupon());
        assertEquals(machine, visitor.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositivePayWithLeftOverCoupon() {
        Visitor ownerVisitor = Visitor.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerVisitor).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Visitor visitor = Visitor.builder().email(EMAIL).money(40).coupon(10)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        visitorService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(30, visitor.getMoney());
        assertEquals(0, visitor.getCoupon());
        assertEquals(20, ownerVisitor.getMoney());
        assertEquals(20, ownerVisitor.getCoupon());
        assertEquals(machine, visitor.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOffMachineNegativeNoVisitor() {
        Long amusementParkId = 10L;
        Long machineId = 0L;

        assertThatThrownBy(() -> visitorService.getOffMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_ON_MACHINE_WITH_ID);

        verify(visitorRepository).findByAmusementParkIdAndMachineIdAndVisitorEmail(amusementParkId, machineId, EMAIL);
    }

    @Test
    public void getOffMachinePositive() {
        Long amusementParkId = 10L;
        Long machineId = 0L;
        Visitor visitor = Visitor.builder().email(EMAIL).machine(Machine.builder().id(machineId).build()).build();
        when(visitorRepository.findByAmusementParkIdAndMachineIdAndVisitorEmail(amusementParkId, machineId, EMAIL))
                .thenReturn(Optional.of(visitor));

        visitorService.getOffMachine(amusementParkId, machineId, EMAIL);

        assertNull(visitor.getMachine());
        verify(visitorRepository).findByAmusementParkIdAndMachineIdAndVisitorEmail(amusementParkId, machineId, EMAIL);
    }

    @Test
    public void leaveParkNegativeNoVisitorInPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> visitorService.leavePark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }

    @Test
    public void leaveParkPositive() {
        Long amusementParkId = 0L;
        Visitor visitor = Visitor.builder().email(EMAIL).amusementPark(AmusementPark.builder().build())
                .money(100).build();
        when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(visitor));

        visitorService.leavePark(amusementParkId, EMAIL);

        assertNull(visitor.getAmusementPark());
        verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, EMAIL);
    }
}