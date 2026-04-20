package hu.beni.amusementpark.test.integration.service;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static hu.beni.amusementpark.constants.StringParamConstants.EMAIL;
import static hu.beni.amusementpark.helper.ValidEntityFactory.createVisitor;
import static org.junit.jupiter.api.Assertions.*;

public class VisitorServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private VisitorService visitorService;

    @Autowired
    private VisitorRepository visitorRepository;

    @Test
    public void findByEmailMakeFreshlyLoggedInTest() {
        assertEquals(testVisitorEmail, visitorService.findByEmailMakeFreshlyLoggedIn(testVisitorEmail).getEmail());
        select++;
        assertStatements();
    }

    @Test
    public void signUpTest() {
        Visitor visitor = visitorService.signUp(createVisitor());
        assertEquals("ROLE_VISITOR", visitor.getAuthority());
        assertEquals(250, visitor.getMoney());

        select += 2;
        insert++;
        assertStatements();
    }

    @Test
    public void uploadMoneyTest() {
        int amountToUpload = 500;
        visitorService.uploadMoney(amountToUpload, testVisitorEmail);
        update++;
        assertStatements();

        assertEquals(visitorSpendingMoney + amountToUpload,
                visitorRepository.findById(testVisitorEmail).get().getMoney());
        select++;
        assertStatements();
    }

    @Test
    public void leaveParkTest() {
        visitorService.leavePark(amusementParkId, inParkVisitorEmail);
        select++;
        update++;
        assertStatements();

        Visitor visitor = visitorRepository.findById(inParkVisitorEmail).get();
        assertNull(visitor.getAmusementPark());
    }

    @Test
    public void enterParkTest() {
        Integer ownerMoney = visitorRepository.findById(EMAIL).get().getMoney();
        reset();

        visitorService.enterPark(amusementParkId, testVisitorEmail);
        select += 4;
        insert++;
        update += 2;
        assertStatements();

        Visitor visitor = visitorRepository.findById(testVisitorEmail).get();
        assertEquals(ownerMoney + amusementParkEntranceFee,
                visitorRepository.findById(EMAIL).get().getMoney());
        assertEquals(visitorSpendingMoney - amusementParkEntranceFee, visitor.getMoney());
        assertNotNull(visitor.getAmusementPark());
    }

    @Test
    public void getOnMachineTest() {
        Integer ownerMoney = visitorRepository.findById(EMAIL).get().getMoney();
        reset();

        visitorService.getOnMachine(amusementParkId, machineId, inParkVisitorEmail);
        select += 3;
        update += 2;
        assertStatements();

        Visitor visitor = visitorRepository.findById(inParkVisitorEmail).get();
        assertEquals(ownerMoney + machineTicketPrice,
                visitorRepository.findById(EMAIL).get().getMoney());
        assertEquals(visitorSpendingMoney - machineTicketPrice, visitor.getMoney());
        assertNotNull(visitor.getMachine());
    }

    @Test
    public void getOffMachine() {
        visitorService.getOffMachine(amusementParkId, machineId, "onMachine@gmail.com");
        select++;
        update++;
        assertStatements();

        Visitor visitor = visitorRepository.findById("onMachine@gmail.com").get();
        assertNull(visitor.getMachine());
    }
}
