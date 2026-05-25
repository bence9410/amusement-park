package hu.bence.amusementpark.test.unit;

import hu.bence.amusementpark.dto.request.ModifyMoneyRequestDto;
import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.*;
import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static hu.bence.amusementpark.constants.StringParamConstants.VALID_PASSWORD;
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
    public void findByNameMakeFreshlyLoggedInNegativeNoUserWithName() {
        assertThatThrownBy(() -> userService.findByNameMakeFreshlyLoggedIn(NAME))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void findByNameMakeFreshlyLoggedInPositive() {
        Users userInDb = Users.builder().name(NAME)
                .amusementPark(AmusementPark.builder().id(0L).build())
                .machine(Machine.builder().build()).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(userInDb));

        Users userResponse = userService.findByNameMakeFreshlyLoggedIn(NAME);

        assertNull(userResponse.getAmusementPark());
        assertNull(userResponse.getMachine());
        verify(userRepository).findById(NAME);
    }

    @Test
    public void signUpNegativeNameAlreadyTaken() {
        Users user = Users.builder().name(NAME).build();
        when(userRepository.countByName(user.getName())).thenReturn(1L);

        assertThatThrownBy(() -> userService.signUp(user, ""))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(NAME_ALREADY_TAKEN, user.getName()));

        verify(userRepository).countByName(user.getName());
    }

    @Test
    public void signUpNegativeWrongCoupon() {
        Users user = Users.builder().name(NAME).password(VALID_PASSWORD).build();

        assertThatThrownBy(() -> userService.signUp(user, "wrong"))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(WRONG_COUPON_CODE);

        verify(userRepository).countByName(user.getName());
    }

    @Test
    public void signUpPositive() {
        Users user = Users.builder().name(NAME).password(VALID_PASSWORD).build();
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.signUp(user, ""));

        assertEquals("ROLE_VISITOR", user.getAuthority());
        assertEquals(0, user.getMoney());
        assertEquals(0, user.getCoupon());
        verify(userRepository).countByName(user.getName());
        verify(userRepository).save(user);
    }

    @Test
    public void signUpPositiveWithCoupon() {
        Users user = Users.builder().name(NAME).password(VALID_PASSWORD).build();
        when(userRepository.save(user)).thenReturn(user);

        assertEquals(user, userService.signUp(user, "EMPLOY_ME"));

        assertEquals("ROLE_VISITOR", user.getAuthority());
        assertEquals(0, user.getMoney());
        assertEquals(10, user.getCoupon());
        verify(userRepository).countByName(user.getName());
        verify(userRepository).save(user);
    }

    @Test
    public void activateCouponNegativeNoUserWithName() {
        assertThatThrownBy(() -> userService.activateCoupon(NAME, ""))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void activateCouponNegativeWrongCoupon() {
        Users user = Users.builder().name(NAME).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.activateCoupon(NAME, "wrong"))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(WRONG_COUPON_CODE);

        verify(userRepository).findById(NAME);
    }

    @Test
    public void activateCouponNegativeAlreadyActivated() {
        Users user = Users.builder().name(NAME).isActivatedCoupon(true).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.activateCoupon(NAME, "EMPLOY_ME"))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(ALREADY_ACTIVATED_COUPON_CODE);

        verify(userRepository).findById(NAME);
    }

    @Test
    public void activateCouponPositive() {
        Users user = Users.builder().name(NAME).coupon(0).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        user = userService.activateCoupon(NAME, "EMPLOY_ME");

        assertEquals(10, user.getCoupon());
        assertTrue(user.isActivatedCoupon());
        verify(userRepository).findById(NAME);
    }

    @Test
    public void enterParkNegativeNoPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void enterParkNegativeNotSignedUp() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_NOT_SIGNED_UP);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
    }

    @Test
    public void enterParkNegativeNotEnoughMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).money(20).coupon(20).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
    }

    @Test
    public void enterParkNegativeUserIsInAPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).money(100).coupon(0).amusementPark(amusementPark).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.enterPark(amusementParkId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_IN_A_PARK);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
    }

    @Test
    public void enterParkPositiveAddUserToKnown() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));

        userService.enterPark(amusementParkId, NAME);

        assertEquals(50, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(amusementParkKnowUserRepository).save(any());
        verify(userRepository).incrementMoneyByEmail(amusementPark.getEntranceFee(), ownerUser.getName());
    }

    @Test
    public void enterParkPositiveUserAlreadyKnown() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).money(100).coupon(0).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, NAME);

        assertEquals(50, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementMoneyByEmail(amusementPark.getEntranceFee(), ownerUser.getName());
    }

    @Test
    public void enterParkPositivePayWithCoupon() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).coupon(50).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, NAME);

        assertEquals(100, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementCouponByEmail(amusementPark.getEntranceFee(), ownerUser.getName());
    }

    @Test
    public void enterParkPositivePayWithLeftOverCoupon() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Users user = Users.builder().name(NAME).coupon(40).money(100).build();
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(amusementPark));
        when(userRepository.findById(NAME)).thenReturn(Optional.of(user));
        when(amusementParkKnowUserRepository.countByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(1L);

        userService.enterPark(amusementParkId, NAME);

        assertEquals(90, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(amusementPark, user.getAmusementPark());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(userRepository).findById(NAME);
        verify(amusementParkKnowUserRepository).countByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementCouponByEmail(40, ownerUser.getName());
        verify(userRepository).incrementMoneyByEmail(10, ownerUser.getName());
    }

    @Test
    public void getOnMachineNegativeNoPark() {
        Long amusementParkId = 0L;
        Long machineId = 1L;

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
    }

    @Test
    public void getOnMachineNegativeNoMachineInPark() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Long machineId = 1L;
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
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

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_IN_PARK_WITH_ID);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void getOnMachineNegativeOnMachine() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).machine(machine).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(amusementParkRepository.findById(amusementParkId))
                .thenReturn(Optional.of(AmusementPark.builder().id(amusementParkId).build()));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_ON_A_MACHINE);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void getOnMachineNegativeNotEnoughMoney() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).money(30).coupon(10).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void getOnMachineNegativeTooYoung() {
        AmusementPark amusementPark = AmusementPark.builder().id(0L).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).money(10).coupon(10).dateOfBirth(LocalDate.now()).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.getOnMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(USER_IS_TOO_YOUNG);

        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void getOnMachinePositive() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).money(40).coupon(0)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, NAME);

        assertEquals(20, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementMoneyByEmail(machine.getTicketPrice(), ownerUser.getName());
    }

    @Test
    public void getOnMachinePositivePayWithCoupon() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).money(40).coupon(20)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, NAME);

        assertEquals(40, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementCouponByEmail(machine.getTicketPrice(), ownerUser.getName());
    }

    @Test
    public void getOnMachinePositivePayWithLeftOverCoupon() {
        Users ownerUser = Users.builder().name("owner").coupon(10).money(10).build();
        AmusementPark amusementPark = AmusementPark.builder().id(0L).owner(ownerUser).build();
        Long amusementParkId = amusementPark.getId();
        Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
        Long machineId = machine.getId();
        Users user = Users.builder().name(NAME).money(40).coupon(10)
                .dateOfBirth(LocalDate.of(1990, 10, 20)).build();
        when(amusementParkRepository.findById(amusementParkId)).thenReturn(Optional.of(amusementPark));
        when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
                .thenReturn(Optional.of(machine));
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        userService.getOnMachine(amusementParkId, machineId, NAME);

        assertEquals(30, user.getMoney());
        assertEquals(0, user.getCoupon());
        assertEquals(machine, user.getMachine());
        verify(amusementParkRepository).findById(amusementParkId);
        verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
        verify(userRepository).incrementCouponByEmail(10, ownerUser.getName());
        verify(userRepository).incrementMoneyByEmail(10, ownerUser.getName());
    }

    @Test
    public void getOffMachineNegativeNoUser() {
        Long amusementParkId = 10L;
        Long machineId = 0L;

        assertThatThrownBy(() -> userService.getOffMachine(amusementParkId, machineId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_ON_MACHINE_WITH_ID);

        verify(userRepository).findByAmusementParkIdAndMachineIdAndUserName(amusementParkId, machineId, NAME);
    }

    @Test
    public void getOffMachinePositive() {
        Long amusementParkId = 10L;
        Long machineId = 0L;
        Users user = Users.builder().name(NAME).machine(Machine.builder().id(machineId).build()).build();
        when(userRepository.findByAmusementParkIdAndMachineIdAndUserName(amusementParkId, machineId, NAME))
                .thenReturn(Optional.of(user));

        userService.getOffMachine(amusementParkId, machineId, NAME);

        assertNull(user.getMachine());
        verify(userRepository).findByAmusementParkIdAndMachineIdAndUserName(amusementParkId, machineId, NAME);
    }

    @Test
    public void leaveParkNegativeNoUserInPark() {
        Long amusementParkId = 0L;

        assertThatThrownBy(() -> userService.leavePark(amusementParkId, NAME))
                .isInstanceOf(AmusementParkException.class).hasMessage(NO_USER_IN_PARK_WITH_ID);

        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void leaveParkPositive() {
        Long amusementParkId = 0L;
        Users user = Users.builder().name(NAME).amusementPark(AmusementPark.builder().build())
                .money(100).build();
        when(userRepository.findByAmusementParkIdAndUserName(amusementParkId, NAME))
                .thenReturn(Optional.of(user));

        userService.leavePark(amusementParkId, NAME);

        assertNull(user.getAmusementPark());
        verify(userRepository).findByAmusementParkIdAndUserName(amusementParkId, NAME);
    }

    @Test
    public void findAllPositive() {
        Page<UserResponseDto> page = new PageImpl<>(
                Collections.singletonList(UserResponseDto.builder().name("Bence").build()));
        Pageable pageable = PageRequest.of(0, 10);
        UserSearchRequestDto dto = new UserSearchRequestDto();
        when(userRepository.findAll(dto, pageable)).thenReturn(page);

        assertEquals(page, userService.findAll(dto, pageable));

        verify(userRepository).findAll(dto, pageable);
    }

    @Test
    public void modifyMoneyNegativeNoUserWithName() {
        ModifyMoneyRequestDto dto = ModifyMoneyRequestDto.builder().userName(NAME).build();
        assertThatThrownBy(() -> userService.modifyMoney(dto))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void modifyMoneyPositive() {
        ModifyMoneyRequestDto dto = ModifyMoneyRequestDto.builder().userName(NAME).value(-100).build();
        Users userInDb = Users.builder().name(NAME).money(1000).build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(userInDb));

        userService.modifyMoney(dto);

        assertEquals(900, userInDb.getMoney());
        verify(userRepository).findById(NAME);
    }

    @Test
    public void makeCreatorNegativeNoUserWithName() {
        assertThatThrownBy(() -> userService.makeCreator(NAME))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(COULD_NOT_FIND_USER, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void makeCreatorNegativeNotVisitor() {
        when(userRepository.findById(NAME)).thenReturn(Optional.of(
                Users.builder().name(NAME).authority("ROLE_ADMIN").build()));

        assertThatThrownBy(() -> userService.makeCreator(NAME))
                .isInstanceOf(AmusementParkException.class)
                .hasMessage(String.format(NOT_VISITOR, NAME));

        verify(userRepository).findById(NAME);
    }

    @Test
    public void makeCreatorPositive() {
        Users userInDb = Users.builder().name(NAME).authority("ROLE_VISITOR").build();
        when(userRepository.findById(NAME)).thenReturn(Optional.of(userInDb));

        userService.makeCreator(NAME);

        assertEquals("ROLE_CREATOR", userInDb.getAuthority());
        verify(userRepository).findById(NAME);
    }
}