package hu.beni.amusementpark.test.rabbitmq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import hu.beni.amusementpark.config.RabbitMQTestConfig.StatisticsReceiver;
import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.clientsupport.dto.VisitorEnterParkEventDTO;
import hu.beni.clientsupport.dto.VisitorGetOnMachineEventDTO;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class RabbitMQStatisticsSenderTests extends AbstractRabbitMQTests {

	@Autowired
	private StatisticsReceiver receiver;

	@Test
	public void test() throws InterruptedException {
		AmusementPark amusementPark = amusementParkService.save(ValidEntityFactory.createAmusementParkWithAddress());
		Machine machine = machineService.addMachine(amusementPark.getId(), ValidEntityFactory.createMachine());
		Visitor visitor = visitorService.signUp(ValidEntityFactory.createVisitor());
		SecurityContextHolder.getContext().setAuthentication(
				new UsernamePasswordAuthenticationToken(new User(visitor.getEmail(), visitor.getPassword(),
						Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority()))), null));

		visitorService.enterPark(amusementPark.getId(), visitor.getId());
		assertEnterParkEventReceivedAndEquals(
				new VisitorEnterParkEventDTO(amusementPark.getId(), visitor.getId(), amusementPark.getEntranceFee()));

		visitorService.getOnMachine(amusementPark.getId(), machine.getId(), visitor.getId());
		assertGetOnMachineEventReceivedAndEquals(new VisitorGetOnMachineEventDTO(amusementPark.getId(), visitor.getId(),
				machine.getTicketPrice(), machine.getId()));
	}

	private void assertEnterParkEventReceivedAndEquals(VisitorEnterParkEventDTO expected) throws InterruptedException {
		assertTrue(receiver.getEnterParkCountDownLatch().await(5, TimeUnit.SECONDS));
		assertEquals(expected, receiver.getVisitorEnterParkEventDTO());
	}

	private void assertGetOnMachineEventReceivedAndEquals(VisitorGetOnMachineEventDTO expected)
			throws InterruptedException {
		assertTrue(receiver.getGetOnMachineCountDownLatch().await(5, TimeUnit.SECONDS));
		assertEquals(expected, receiver.getVisitorGetOnMachineEventDTO());
	}

}
