package hu.bence.amusementpark.test.integration.service;

import hu.bence.amusementpark.dto.request.ModifyMoneyRequestDto;
import hu.bence.amusementpark.dto.request.UserSearchRequestDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.service.UserService;
import hu.bence.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static hu.bence.amusementpark.constants.StringParamConstants.CREATOR;
import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static hu.bence.amusementpark.helper.ValidEntityFactory.createUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private UserService userService;

    @Test
    public void findByNameMakeFreshlyLoggedInTest() {
        assertEquals(testVisitorName, userService.findByNameMakeFreshlyLoggedIn(testVisitorName).getName());
        select += 2;
        assertStatements();
    }

    @Test
    public void signUpTest() {
        Users user = userService.signUp(createUser(), "");
        assertEquals("ROLE_VISITOR", user.getAuthority());
        assertEquals(0, user.getMoney());
        assertEquals(0, user.getCoupon());

        select += 2;
        insert++;
        assertStatements();
    }

    @Test
    public void activateCouponTest() {
        Users user = userService.activateCoupon(NAME, "EMPLOY_ME");
        select++;
        update++;
        assertStatements();

        assertEquals(10, user.getCoupon());
        assertTrue(user.isActivatedCoupon());
    }

    @Test
    public void leaveParkTest() {
        userService.leavePark(amusementParkId, inParkVisitorName);
        select++;
        update++;
        assertStatements();

        Users user = userRepository.findById(inParkVisitorName).get();
        assertNull(user.getAmusementPark());
    }

    @Test
    public void enterParkTest() {
        Integer ownerMoney = userRepository.findById(CREATOR).get().getMoney();
        reset();

        userService.enterPark(amusementParkId, testVisitorName);
        select += 4;
        insert++;
        update += 2;
        assertStatements();

        Users user = userRepository.findById(testVisitorName).get();
        assertEquals(ownerMoney + amusementParkEntranceFee,
                userRepository.findById(CREATOR).get().getMoney());
        assertEquals(userMoney - amusementParkEntranceFee, user.getMoney());
        assertNotNull(user.getAmusementPark());
    }

    @Test
    public void getOnMachineTest() {
        Integer ownerMoney = userRepository.findById(CREATOR).get().getMoney();
        reset();

        userService.getOnMachine(amusementParkId, machineId, inParkVisitorName);
        select += 3;
        update += 2;
        assertStatements();

        Users user = userRepository.findById(inParkVisitorName).get();
        assertEquals(ownerMoney + machineTicketPrice,
                userRepository.findById(CREATOR).get().getMoney());
        assertEquals(userMoney - machineTicketPrice, user.getMoney());
        assertNotNull(user.getMachine());
    }

    @Test
    public void getOffMachine() {
        userService.getOffMachine(amusementParkId, machineId, "onMachine");
        select++;
        update++;
        assertStatements();

        Users user = userRepository.findById("onMachine").get();
        assertNull(user.getMachine());
    }

    @Test
    public void findAll() {
        UserSearchRequestDto dto = UserSearchRequestDto.builder().name(NAME).build();

        Page<UserResponseDto> page = userService.findAll(dto, PageRequest.of(0, 10));
        select += 2;
        assertStatements();

        assertEquals(1, page.getTotalElements());
    }

    @Test
    public void modifyMoney() {
        ModifyMoneyRequestDto dto = ModifyMoneyRequestDto.builder().userName(NAME).value(-100).build();

        userService.modifyMoney(dto);
        select++;
        update++;
        assertStatements();

        assertEquals(900, userRepository.findById(NAME).get().getMoney());
    }

    @Test
    public void makeCreator() {
        userService.makeCreator(testVisitorName);
        select++;
        update++;
        assertStatements();

        assertEquals("ROLE_CREATOR", userRepository.findById(testVisitorName).get().getAuthority());
    }
}
