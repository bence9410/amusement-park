package hu.beni.amusementpark.test.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.enums.VisitorEventType;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class VisitorServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private VisitorService visitorService;

	@Test
	public void findByEmailTest() {
		assertEquals(testVisitorEmail, visitorService.findByEmail(testVisitorEmail).getEmail());
		select++;
		assertStatements();
	}

	@Test
	public void signUpTest() {
		Visitor visitor = visitorService.signUp(ValidEntityFactory.createVisitor());
		assertEquals("ROLE_VISITOR", visitor.getAuthority());
		assertEquals(250, visitor.getSpendingMoney().intValue());
		select += 2;
		insert++;
		assertStatements();
	}

	@Test
	public void uploadMoneyTest() {
		Integer ammountToUpload = 500;
		visitorService.uploadMoney(ammountToUpload, testVisitorEmail);
		update++;
		assertStatements();

		assertEquals(visitorSpendingMoney + ammountToUpload,
				visitorService.findByEmail(testVisitorEmail).getSpendingMoney().intValue());
		select++;
		assertStatements();
	}

	@Test
	public void leaveParkTest() {
		assertNull(visitorService.leavePark(amusementParkId, inParkVisitorEmail).getAmusementPark());
		select++;
		insert++;
		update++;
		assertStatements();

		Visitor visitor = visitorService.findByEmail(inParkVisitorEmail);
		assertNull(visitor.getAmusementPark());
		assertEquals(1, visitor.getVisitorEvents().size());
		assertEquals(VisitorEventType.LEAVE_PARK, visitor.getVisitorEvents().iterator().next().getType());
	}

	@Test
	public void enterParkTest() {
		assertNotNull(visitorService.enterPark(amusementParkId, testVisitorEmail).getAmusementPark());
		select += 4;
		insert += 2;
		update += 2;
		assertStatements();

		Visitor visitor = visitorService.findByEmail(testVisitorEmail);
		assertEquals(amusementParkCapital + amusementParkEntranceFee,
				amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
		assertEquals(visitorSpendingMoney - amusementParkEntranceFee, visitor.getSpendingMoney().intValue());
		assertNotNull(visitor.getAmusementPark());
		assertEquals(1, visitor.getVisitorEvents().size());
		assertEquals(VisitorEventType.ENTER_PARK, visitor.getVisitorEvents().iterator().next().getType());
	}

	@Test
	public void getOnMachineTest() {
		assertNotNull(visitorService.getOnMachine(amusementParkId, machineId, inParkVisitorEmail));
		select += 3;
		insert++;
		update += 2;
		assertStatements();

		Visitor visitor = visitorService.findByEmail(inParkVisitorEmail);
		assertEquals(amusementParkCapital + machineTicketPrice,
				amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
		assertEquals(visitorSpendingMoney - machineTicketPrice, visitor.getSpendingMoney().intValue());
		assertNotNull(visitor.getMachine());
		assertEquals(1, visitor.getVisitorEvents().size());
		assertEquals(VisitorEventType.GET_ON_MACHINE, visitor.getVisitorEvents().iterator().next().getType());
	}

	@Test
	public void getOffMachine() {
		assertNull(visitorService.getOffMachine(amusementParkId, machineId, "onMachine@gmail.com").getMachine());
		select++;
		insert++;
		update++;
		assertStatements();

		Visitor visitor = visitorService.findByEmail("onMachine@gmail.com");
		assertNull(visitor.getMachine());
		assertEquals(1, visitor.getVisitorEvents().size());
		assertEquals(VisitorEventType.GET_OFF_MACHINE, visitor.getVisitorEvents().iterator().next().getType());
	}

	@Test
	public void findAllVisitorTest() {
		List<Visitor> visitors = visitorService.findAllVisitor();
		assertEquals(5, visitors.size());
		assertFalse(visitors.stream().map(Visitor::getAuthority).anyMatch("ROLE_ADMIN"::equals));
		select++;
		assertStatements();
	}

	@Test
	public void deleteTest() {
		visitorService.delete(testVisitorEmail);
		select++;
		delete++;
		assertStatements();
	}
}
