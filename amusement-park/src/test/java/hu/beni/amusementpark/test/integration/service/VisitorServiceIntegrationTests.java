package hu.beni.amusementpark.test.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import hu.beni.amusementpark.entity.Visitor;
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
		visitorService.uploadMoney(500, testVisitorEmail);
		update++;
		assertStatements();

		assertEquals(1500, visitorService.findByEmail(testVisitorEmail).getSpendingMoney().intValue());
		select++;
		assertStatements();
	}

	@Test
	public void leaveParkTest() {
		assertNull(visitorService.leavePark(10L, inParkVisitorEmail).getAmusementPark());
		select++;
		update++;
		assertStatements();
	}

	@Test
	public void enterParkTest() {
		assertNotNull(visitorService.enterPark(amusementParkId, testVisitorEmail).getAmusementPark());
		select += 4;
		insert++;
		update += 2;
		assertStatements();

		assertEquals(5000 + 200, amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
		assertEquals(1000 - 200, visitorService.findByEmail(testVisitorEmail).getSpendingMoney().intValue());
	}

	@Test
	public void getOnMachineTest() {
		assertNotNull(visitorService.getOnMachine(amusementParkId, machineId, inParkVisitorEmail));
		select += 3;
		update += 2;
		assertStatements();

		assertEquals(amusementParkCapital + machineTicketPrice,
				amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
		assertEquals(visitorSpendingMoney - machineTicketPrice,
				visitorService.findByEmail(inParkVisitorEmail).getSpendingMoney().intValue());
	}

	@Test
	public void getOffMachine() {
		assertNull(visitorService.getOffMachine(machineId, "onMachine@gmail.com").getMachine());
		select++;
		update++;
		assertStatements();
	}

	@Test
	public void findAllVisitorTest() {
		List<Visitor> visitors = visitorService.findAllVisitor();
		assertEquals(3, visitors.size());
		assertFalse(visitors.stream().anyMatch(v -> v.getAuthority().equals("ROLE_ADMIN")));
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
