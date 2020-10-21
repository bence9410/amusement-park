package hu.beni.amusementpark.test.integration.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import hu.beni.amusementpark.dto.request.MachineSearchRequestDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.entity.Machine;
import hu.beni.amusementpark.helper.ValidEntityFactory;
import hu.beni.amusementpark.service.MachineService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class MachineServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private MachineService machineService;

	@Test
	public void addMachineTest() {
		Machine machine = machineService.addMachine(amusementParkId, ValidEntityFactory.createMachine());
		assertNotNull(machine.getId());
		assertNotNull(machine.getAmusementPark());
		assertEquals(amusementParkId, machine.getAmusementPark().getId().longValue());
		select += 2;
		update++;
		insert++;
		assertStatements();

		assertEquals(amusementParkCapital - machine.getPrice(),
				amusementParkRepository.findById(amusementParkId).get().getCapital().intValue());
	}

	@Test
	public void findByIdTest() {
		assertEquals("test Titanic", machineService.findById(amusementParkId, machineId).getFantasyName());
		select++;
		assertStatements();
	}

	@Test
	public void findAllTest() {
		MachineSearchRequestDto dto = new MachineSearchRequestDto();
		dto.setAmusementParkId(amusementParkId);
		dto.setFantasyName("tani");

		Page<MachineSearchResponseDto> page = machineService.findAll(dto, PageRequest.of(0, 10));

		assertEquals(1, page.getTotalElements());

		select += 2;
		assertStatements();
	}

}
