package hu.beni.amusementpark.test.unit;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.CAN_NOT_DELETE_ADMIN;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.EMAIL_ALREADY_TAKEN;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NOT_ENOUGH_MONEY;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_AMUSEMENT_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_FREE_SEAT_ON_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_MACHINE_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_IN_PARK_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.NO_VISITOR_ON_MACHINE_WITH_ID;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_IN_A_PARK;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_ON_A_MACHINE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_IS_TOO_YOUNG;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.VISITOR_NOT_SIGNED_UP;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import hu.beni.amusementpark.entity.AmusementPark;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.entity.VisitorEvent;
import hu.beni.amusementpark.enums.VisitorEventType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.repository.AmusementParkKnowVisitorRepository;
import hu.beni.amusementpark.repository.AmusementParkRepository;
import hu.beni.amusementpark.repository.MachineRepository;
import hu.beni.amusementpark.repository.VisitorEventRepository;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import hu.beni.amusementpark.service.impl.VisitorServiceImpl;

public class VisitorServiceUnitTests {

	private AmusementParkRepository amusementParkRepository;
	private MachineRepository machineRepository;
	private VisitorRepository visitorRepository;
	private AmusementParkKnowVisitorRepository amusementParkKnowVisitorRepository;
	private VisitorEventRepository visitorEventRepository;

	private VisitorService visitorService;

	@Before
	public void setUp() {
		amusementParkRepository = mock(AmusementParkRepository.class);
		machineRepository = mock(MachineRepository.class);
		visitorRepository = mock(VisitorRepository.class);
		amusementParkKnowVisitorRepository = mock(AmusementParkKnowVisitorRepository.class);
		visitorEventRepository = mock(VisitorEventRepository.class);
		visitorService = new VisitorServiceImpl(amusementParkRepository, machineRepository, visitorRepository,
				amusementParkKnowVisitorRepository, visitorEventRepository);
	}

	@After
	public void verifyNoMoreInteractionsOnMocks() {
		verifyNoMoreInteractions(amusementParkRepository, machineRepository, visitorRepository,
				amusementParkKnowVisitorRepository);
	}

	@Test
	public void findByEmailNegativeNoVisitorWithUsername() {
		String email = "nembence1994@gmail.com";

		assertThatThrownBy(() -> visitorService.findByEmail(email)).isInstanceOf(AmusementParkException.class)
				.hasMessage(String.format(COULD_NOT_FIND_USER, email));

		verify(visitorRepository).findById(email);
	}

	@Test
	public void findByEmailPositive() {
		Visitor visitor = Visitor.builder().email("nembence1994@gmail.com").build();
		String email = visitor.getEmail();

		when(visitorRepository.findById(email)).thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.findByEmail(email));

		verify(visitorRepository).findById(email);
	}

	@Test
	public void signUpNegativeEmailAlreadyTaken() {
		Visitor visitor = Visitor.builder().email("nembence1994@gmail.com").build();

		when(visitorRepository.countByEmail(visitor.getEmail())).thenReturn(1L);

		assertThatThrownBy(() -> visitorService.signUp(visitor)).isInstanceOf(AmusementParkException.class)
				.hasMessage(String.format(EMAIL_ALREADY_TAKEN, visitor.getEmail()));

		verify(visitorRepository).countByEmail(visitor.getEmail());
	}

	@Test
	public void signUpPositive() {
		Visitor visitor = Visitor.builder().email("nembence1994@gmail.com").build();

		when(visitorRepository.save(visitor)).thenReturn(visitor);

		assertEquals(visitor, visitorService.signUp(visitor));

		verify(visitorRepository).countByEmail(visitor.getEmail());
		verify(visitorRepository).save(visitor);
	}

	@Test
	public void enterParkNegativeNoPark() {
		Long amusementParkId = 0L;
		String visitorEmail = "benike@gmail.com";

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_AMUSEMENT_PARK_WITH_ID);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
	}

	@Test
	public void enterParkNegativeNotSignedUp() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		String visitorEmail = "benike@gmail.com";

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_NOT_SIGNED_UP);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorEmail);
	}

	@Test
	public void enterParkNegativeNotEnoughMoney() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(20).build();
		String visitorEmail = visitor.getEmail();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorEmail);
	}

	@Test
	public void enterParkNegativeVisitorIsInAPark() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(100).amusementPark(amusementPark)
				.build();
		String visitorEmail = visitor.getEmail();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.enterPark(amusementParkId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_IN_A_PARK);

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorEmail);
	}

	@Test
	public void enterParkPositiveAddVisitorToKnown() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(100).build();
		String visitorEmail = visitor.getEmail();
		Integer spendingMoney = visitor.getSpendingMoney();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.enterPark(amusementParkId, visitorEmail));

		assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney().longValue());
		assertEquals(amusementPark, visitor.getAmusementPark());

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorEmail);
		verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
		verify(amusementParkKnowVisitorRepository).save(any());
		verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
		verify(visitorEventRepository).save(VisitorEvent.builder().type(VisitorEventType.ENTER_PARK)
				.amusementPark(amusementPark).visitor(visitor).build());
	}

	@Test
	public void enterParkPositiveVisitorAlreadyKnown() {
		AmusementPark amusementPark = AmusementPark.builder().id(0L).entranceFee(50).build();
		Long amusementParkId = amusementPark.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(100).build();
		String visitorEmail = visitor.getEmail();
		Long numberOfKnowsVisitorsById = 1L;
		Integer spendingMoney = visitor.getSpendingMoney();

		when(amusementParkRepository.findByIdReadOnlyIdAndEntranceFee(amusementParkId))
				.thenReturn(Optional.of(amusementPark));
		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));
		when(amusementParkKnowVisitorRepository.countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(numberOfKnowsVisitorsById);

		assertEquals(visitor, visitorService.enterPark(amusementParkId, visitorEmail));

		assertEquals(spendingMoney - amusementPark.getEntranceFee(), visitor.getSpendingMoney().longValue());
		assertEquals(amusementPark, visitor.getAmusementPark());

		verify(amusementParkRepository).findByIdReadOnlyIdAndEntranceFee(amusementParkId);
		verify(visitorRepository).findById(visitorEmail);
		verify(amusementParkKnowVisitorRepository).countByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
		verify(amusementParkRepository).incrementCapitalById(amusementPark.getEntranceFee(), amusementParkId);
		verify(visitorEventRepository).save(VisitorEvent.builder().type(VisitorEventType.ENTER_PARK)
				.amusementPark(amusementPark).visitor(visitor).build());
	}

	@Test
	public void getOnMachineNegativeNoMachineInPark() {
		Long amusementParkId = 0L;
		Long machineId = 1L;
		String visitorEmail = "benike@gmail.com";

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_MACHINE_IN_PARK_WITH_ID);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
	}

	@Test
	public void getOnMachineNegativeNoVisitorInPark() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).build();
		Long machineId = machine.getId();
		String visitorEmail = "benike@gmail.com";

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
	}

	@Test
	public void getOnMachineNegativeOnMachine() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").machine(machine).build();
		String visitorEmail = visitor.getEmail();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_ON_A_MACHINE);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
	}

	@Test
	public void getOnMachineNegativeNotEnoughtMoney() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(50).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(40).build();
		String visitorEmail = visitor.getEmail();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NOT_ENOUGH_MONEY);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
	}

	@Test
	public void getOnMachineNegativeTooYoung() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(40).dateOfBirth(LocalDate.now())
				.build();
		String visitorEmail = visitor.getEmail();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(VISITOR_IS_TOO_YOUNG);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
	}

	@Test
	public void getOnMachineNegativeNoFreeSeat() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(40)
				.dateOfBirth(LocalDate.of(1990, 10, 20)).build();
		String visitorEmail = visitor.getEmail();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));
		when(visitorRepository.countByMachineId(machineId)).thenReturn(10L);

		assertThatThrownBy(() -> visitorService.getOnMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_FREE_SEAT_ON_MACHINE);

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
		verify(visitorRepository).countByMachineId(machineId);
	}

	@Test
	public void getOnMachinePositive() {
		Long amusementParkId = 0L;
		Machine machine = Machine.builder().id(1L).ticketPrice(20).minimumRequiredAge(20).numberOfSeats(10).build();
		Long machineId = machine.getId();
		Visitor visitor = Visitor.builder().email("benike@gmail.com").spendingMoney(40)
				.dateOfBirth(LocalDate.of(1990, 10, 20)).build();
		String visitorEmail = visitor.getEmail();
		Integer spendingMoney = visitor.getSpendingMoney();

		when(machineRepository.findByAmusementParkIdAndMachineId(amusementParkId, machineId))
				.thenReturn(Optional.of(machine));
		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));
		when(visitorRepository.countByMachineId(machineId)).thenReturn(1L);

		assertEquals(visitor, visitorService.getOnMachine(amusementParkId, machineId, visitorEmail));

		assertEquals(spendingMoney - machine.getTicketPrice(), visitor.getSpendingMoney().longValue());
		assertEquals(machine, visitor.getMachine());

		verify(machineRepository).findByAmusementParkIdAndMachineId(amusementParkId, machineId);
		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
		verify(visitorRepository).countByMachineId(machineId);
		verify(amusementParkRepository).incrementCapitalById(machine.getTicketPrice(), amusementParkId);
		verify(visitorEventRepository).save(VisitorEvent.builder().type(VisitorEventType.GET_ON_MACHINE)
				.amusementPark(AmusementPark.builder().id(amusementParkId).build()).machine(machine).visitor(visitor)
				.build());
	}

	@Test
	public void getOffMachineNegativeNoVisitor() {
		Long amusementParkId = 10L;
		Long machineId = 0L;
		String visitorEmail = "benike@gmail.com";

		assertThatThrownBy(() -> visitorService.getOffMachine(amusementParkId, machineId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_ON_MACHINE_WITH_ID);

		verify(visitorRepository).findByMachineIdAndVisitorEmail(machineId, visitorEmail);
	}

	@Test
	public void getOffMachinePositive() {
		Long amusementParkId = 10L;
		Long machineId = 0L;
		Visitor visitor = Visitor.builder().email("benike@gmail.com").machine(Machine.builder().id(machineId).build())
				.build();
		String visitorEmail = visitor.getEmail();

		when(visitorRepository.findByMachineIdAndVisitorEmail(machineId, visitorEmail))
				.thenReturn(Optional.of(visitor));

		assertEquals(visitor, visitorService.getOffMachine(amusementParkId, machineId, visitorEmail));

		assertNull(visitor.getMachine());

		verify(visitorRepository).findByMachineIdAndVisitorEmail(machineId, visitorEmail);
		verify(visitorEventRepository).save(VisitorEvent.builder().type(VisitorEventType.GET_OFF_MACHINE)
				.amusementPark(AmusementPark.builder().id(amusementParkId).build())
				.machine(Machine.builder().id(machineId).build()).visitor(visitor).build());
	}

	@Test
	public void leaveParkNegativeNoVisitorInPark() {
		Long amusementParkId = 0L;
		String visitorEmail = "benike@gmail.com";

		assertThatThrownBy(() -> visitorService.leavePark(amusementParkId, visitorEmail))
				.isInstanceOf(AmusementParkException.class).hasMessage(NO_VISITOR_IN_PARK_WITH_ID);

		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
	}

	@Test
	public void leaveParkPositive() {
		Long amusementParkId = 0L;
		Visitor visitor = Visitor.builder().email("benike@gmail.com").amusementPark(AmusementPark.builder().build())
				.spendingMoney(100).build();
		String visitorEmail = visitor.getEmail();

		when(visitorRepository.findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail))
				.thenReturn(Optional.of(visitor));

		visitorService.leavePark(amusementParkId, visitorEmail);

		assertNull(visitor.getAmusementPark());
		assertNotNull(visitor.getSpendingMoney());

		verify(visitorRepository).findByAmusementParkIdAndVisitorEmail(amusementParkId, visitorEmail);
		verify(visitorEventRepository).save(VisitorEvent.builder().type(VisitorEventType.LEAVE_PARK)
				.amusementPark(AmusementPark.builder().id(amusementParkId).build()).visitor(visitor).build());
	}

	@Test
	public void findAllPositive() {
		List<Visitor> visitors = Arrays.asList(Visitor.builder().email("benike@gmail.com").build(),
				Visitor.builder().email("jenike@gmail.com").build());

		when(visitorRepository.findAllVisitor()).thenReturn(visitors);

		assertEquals(visitors, visitorService.findAllVisitor());

		verify(visitorRepository).findAllVisitor();
	}

	@Test
	public void deleteNegativeNotSignedUp() {
		String visitorEmail = "benike@gmail.com";

		assertThatThrownBy(() -> visitorService.delete(visitorEmail)).isInstanceOf(AmusementParkException.class)
				.hasMessage(VISITOR_NOT_SIGNED_UP);

		verify(visitorRepository).findById(visitorEmail);
	}

	@Test
	public void deleteNegativeCanNotDeleteAdmin() {
		Visitor visitor = Visitor.builder().email("benike@gmail.com").authority("ROLE_ADMIN").build();

		when(visitorRepository.findById(visitor.getEmail())).thenReturn(Optional.of(visitor));

		assertThatThrownBy(() -> visitorService.delete(visitor.getEmail())).isInstanceOf(AmusementParkException.class)
				.hasMessage(CAN_NOT_DELETE_ADMIN);

		verify(visitorRepository).findById(visitor.getEmail());
	}

	@Test
	public void deletePositive() {
		Visitor visitor = Visitor.builder().email("benike@gmail.com").authority("ROLE_VISITOR").build();
		String visitorEmail = visitor.getEmail();

		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

		visitorService.delete(visitorEmail);

		verify(visitorRepository).findById(visitorEmail);
		verify(visitorRepository).deleteById(visitorEmail);
	}

	@Test
	public void getOffMachineAndLeaveParkPositive() {
		Visitor visitor = Visitor.builder().email("benike@gmail.com").amusementPark(AmusementPark.builder().build())
				.machine(Machine.builder().build()).build();
		String visitorEmail = visitor.getEmail();

		when(visitorRepository.findById(visitorEmail)).thenReturn(Optional.of(visitor));

		visitorService.getOffMachineAndLeavePark(visitorEmail);
		assertNull(visitor.getAmusementPark());
		assertNull(visitor.getMachine());

		verify(visitorRepository).findById(visitorEmail);
	}
}