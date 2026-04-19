package hu.beni.amusementpark.test.integration.service;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        Visitor visitor = visitorService.signUp(ValidEntityFactory.createVisitor());
        assertEquals("ROLE_VISITOR", visitor.getAuthority());
        assertEquals(250, visitor.getSpendingMoney());

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
                visitorService.findByEmailMakeFreshlyLoggedIn(testVisitorEmail).getSpendingMoney());
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
        visitorService.enterPark(amusementParkId, testVisitorEmail);
        select += 4;
        insert++;
        update += 2;
        assertStatements();

        Visitor visitor = visitorRepository.findById(testVisitorEmail).get();
        assertEquals(amusementParkCapital + amusementParkEntranceFee,
                amusementParkRepository.findById(amusementParkId).get().getCapital());
        assertEquals(visitorSpendingMoney - amusementParkEntranceFee, visitor.getSpendingMoney());
        assertNotNull(visitor.getAmusementPark());
    }

    @Test
    public void getOnMachineTest() {
        visitorService.getOnMachine(amusementParkId, machineId, inParkVisitorEmail);
        select += 2;
        update += 2;
        assertStatements();

        Visitor visitor = visitorRepository.findById(inParkVisitorEmail).get();
        assertEquals(amusementParkCapital + machineTicketPrice,
                amusementParkRepository.findById(amusementParkId).get().getCapital());
        assertEquals(visitorSpendingMoney - machineTicketPrice, visitor.getSpendingMoney());
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
