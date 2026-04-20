package hu.bence.amusementpark.test.unit;

import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.entity.Machine;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.repository.AmusementParkKnowUserRepository;
import hu.bence.amusementpark.repository.AmusementParkRepository;
import hu.bence.amusementpark.repository.MachineRepository;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.UserService;
import hu.bence.amusementpark.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.*;
import static hu.bence.amusementpark.constants.StringParamConstants.EMAIL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceUnitTests {

    private AmusementParkRepository amusementParkRepository;
    private MachineRepository machineRepository;
    private UserRepository userRepository;
    private AmusementParkKnowUserRepository amusementParkKnowUserRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        amusementParkRepository = mock(AmusementParkRepository.class);
        machineRepository = mock(MachineRepository.class);
        userRepository = mock(UserRepository.class);
        amusementParkKnowUserRepository = mock(AmusementParkKnowUserRepository.class);
        userService = new UserServiceImpl(amusementParkRepository, machineRepository, userRepository,
                amusementParkKnowUserRepository, new BCryptPasswordEncoder());
    }

    @AfterEach
    public void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(amusementParkRepository, machineRepository, userRepository,
                amusementParkKnowUserRepository);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInNegativeNoUserWithEmail() {
        assertThatThrownBy(() -> userService.findByEmailMakeFreshlyLoggedIn(EMAIL))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, EMAIL));

        verify(userRepository).findById(EMAIL);
    }

    @Test
    public void findByEmailMakeFreshlyLoggedInPositive() {
        Users userRequest = Users.builder().email(EMAIL).build();
        Users userInDb = Users.builder().email(EMAIL)
                .amusementPark(AmusementPark.builder().id(0L).build())
                .machine(Machine.builder().build()).build();
        ;
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(userInDb));

        Users userResponse = userService.findByEmailMakeFreshlyLoggedIn(EMAIL);

        assertNull(userResponse.getAmusementPark());
        assertNull(userResponse.getMachine());
        verify(userRepository).findById(EMAIL);
    }

    @Test
    public void signUpNegativeEmailAlreadyTaken() {
        Users user = Users.builder().email(EMAIL).build();
        when(userRepository.countByEmail(user.getEmail())).thenReturn(1L);

        assertThatThrownBy(() -> userService.signUp(user)).isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(EMAIL_ALREADY_TAKEN, user.getEmail()));

        verify(userRepository).countByEmail(user.getEmail());
    }

    @Test
    public void signUpPositive() {
        Users user = Users.builder().email(EMAIL).password("Pass1234").build();
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.signUp(user));

        assertNotNull(user.getMoney());
        assertNotNull(user.getAuthority());
        verify(userRepository).countByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    public void enterParkNegativeNoPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void enterParkNegativeNotSignedUp() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_NOT_SIGNED_UP);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
    }

    @Test
    public void enterParkNegativeNotEnoughMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).money(20).coupon(20).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
    }

    @Test
    public void enterParkNegativeUserIsInAPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).money(100).coupon(0).amusementPark(amusementPark).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_IN_A_PARK);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
    }

    @Test
    public void enterParkPositiveAddUserToKnown() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));

        userService.enterPark(amusementParkId, EMAIL);

        assertEquals(50, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(60, ownerUser.getMoney());
        assertEquals(10, ownerUser.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
        verify(amusementParkKnowUserRepository).save(any());
    }

    @Test
    public void enterParkPositiveUserAlreadyKnown() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, EMAIL);

        assertEquals(50, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(60, ownerUser.getMoney());
        assertEquals(10, ownerUser.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void enterParkPositivePayWithCoupon() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).coupon(50).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, EMAIL);

        assertEquals(100, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(10, ownerUser.getMoney());
        assertEquals(60, ownerUser.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void enterParkPositivePayWithLeftOverCoupon() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().email(EMAIL).coupon(40).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(EMAIL)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, EMAIL);

        assertEquals(90, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(20, ownerUser.getMoney());
        assertEquals(50, ownerUser.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(EMAIL);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void getOnMachineNegativeNoMachineInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Long machineId = 1L;
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
    }

    @Test
    public void getOnMachineNegativeNoUserInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeOnMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).machine(machine).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(AmusementPark.builder().id(amusementParkId).build()));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_ON_A_MACHINE);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeNotEnoughtMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).money(30).coupon(10).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachineNegativeTooYoung() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).money(10).coupon(10).dateOfBirth(LocalDate.now()).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_TOO_YOUNG);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositive() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).money(40).coupon(0)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(20, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(30, ownerUser.getMoney());
        assertEquals(10, ownerUser.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositivePayWithCoupon() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).money(40).coupon(20)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(40, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(10, ownerUser.getMoney());
        assertEquals(30, ownerUser.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOnMachinePositivePayWithLeftOverCoupon() {
        Users ownerUser = Users.builder().coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().email(EMAIL).money(40).coupon(10)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, EMAIL);

        assertEquals(30, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(20, ownerUser.getMoney());
        assertEquals(20, ownerUser.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void getOffMachineNegativeNoUser() {
        Long amusementParkId = 10L;
        Long machineId = 0L;

        assertThatThrownBy(() -> userService.getOffMachine(amusementParkId, machineId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_ON_MACHINE_WITH_ID);

        verify(userRepository).findByAmusementParkIdAndMachineIdAndUserEmail(amusementParkId, machineId, EMAIL);
    }

    @Test
    public void getOffMachinePositive() {
        Long amusementParkId = 10L;
        Long machineId = 0L;
        Users user = Users.builder().email(EMAIL).machine(Machine.builder().id(machineId).build()).build();
        when(userRepository.findByAmusementParkIdAndMachineIdAndUserEmail(amusementParkId, machineId, EMAIL))
                .thenReturn(Optional.of(user));

        userService.getOffMachine(amusementParkId, machineId, EMAIL);

        assertNull(user.getMachine());
        verify(userRepository).findByAmusementParkIdAndMachineIdAndUserEmail(amusementParkId, machineId, EMAIL);
    }

    @Test
    public void leaveParkNegativeNoUserInPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> userService.leavePark(amusementParkId, EMAIL))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_IN_PARK_WITH_ID);

        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }

    @Test
    public void leaveParkPositive() {
        Long amusementParkId = 0L;
        Users user = Users.builder().email(EMAIL).amusementPark(AmusementPark.builder().build())
                .money(100).build();
        when(userRepository.findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL))
                .thenReturn(Optional.of(user));

        userService.leavePark(amusementParkId, EMAIL);

        assertNull(user.getAmusementPark());
        verify(userRepository).findByAmusementParkIdAndUserEmail(amusementParkId, EMAIL);
    }
}