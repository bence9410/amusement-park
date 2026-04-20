package hu.bence.amusementpark.test.integration.service;

import hu.bence.amusementpark.entity.Users;
import hu.bence.amusementpark.repository.UserRepository;
import hu.bence.amusementpark.service.UserService;
import hu.bence.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static hu.bence.amusementpark.helper.ValidEntityFactory.createUser;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void findByNameMakeFreshlyLoggedInTest() {
        assertEquals(testUserName, userService.findByNameMakeFreshlyLoggedIn(testUserName).getName());
        select++;
        assertStatements();
    }

    @Test
    public void signUpTest() {
        Users user = userService.signUp(createUser());
        assertEquals("ROLE_VISITOR", user.getAuthority());
        assertEquals(250, user.getMoney());

        select += 2;
        insert++;
        assertStatements();
    }

    @Test
    public void uploadMoneyTest() {
        int amountToUpload = 500;
        userService.uploadMoney(amountToUpload, testUserName);
        update++;
        assertStatements();

        assertEquals(userMoney + amountToUpload,
                userRepository.findById(testUserName).get().getMoney());
        select++;
        assertStatements();
    }

    @Test
    public void leaveParkTest() {
        userService.leavePark(amusementParkId, inParkUserName);
        select++;
        update++;
        assertStatements();

        Users user = userRepository.findById(inParkUserName).get();
        assertNull(user.getAmusementPark());
    }

    @Test
    public void enterParkTest() {
        Integer ownerMoney = userRepository.findById(NAME).get().getMoney();
        reset();

        userService.enterPark(amusementParkId, testUserName);
        select += 4;
        insert++;
        update += 2;
        assertStatements();

        Users user = userRepository.findById(testUserName).get();
        assertEquals(ownerMoney + amusementParkEntranceFee,
                userRepository.findById(NAME).get().getMoney());
        assertEquals(userMoney - amusementParkEntranceFee, user.getMoney());
        assertNotNull(user.getAmusementPark());
    }

    @Test
    public void getOnMachineTest() {
        Integer ownerMoney = userRepository.findById(NAME).get().getMoney();
        reset();

        userService.getOnMachine(amusementParkId, machineId, inParkUserName);
        select += 3;
        update += 2;
        assertStatements();

        Users user = userRepository.findById(inParkUserName).get();
        assertEquals(ownerMoney + machineTicketPrice,
                userRepository.findById(NAME).get().getMoney());
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
}
