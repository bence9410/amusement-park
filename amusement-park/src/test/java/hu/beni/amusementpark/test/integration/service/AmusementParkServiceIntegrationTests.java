package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class AmusementParkServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private AmusementParkService amusementParkService;

	@Test
	public void saveTest() {
		assertNotNull(amusementParkService.save(createAmusementPark()).getId());
		insert++;
		assertStatements();
	}

	@Test
	public void findByIdTest() {
		assertEquals("test park 100", amusementParkService.findById(amusementParkId).getName());
		select++;
		assertStatements();
	}

	@Test
	public void findDetailByIdTest() {
		AmusementParkDetailResponseDto amusementParkDetailResponseDto = amusementParkService
				.findDetailById(amusementParkId);

		assertEquals("test park 100", amusementParkDetailResponseDto.getName());
		assertEquals(1, amusementParkDetailResponseDto.getNumberOfMachines().intValue());
		assertEquals(2, amusementParkDetailResponseDto.getNumberOfGuestBookRegistries().intValue());
		assertEquals(2, amusementParkDetailResponseDto.getNumberOfActiveVisitors().intValue());
		assertEquals(2, amusementParkDetailResponseDto.getNumberOfKnownVisitors().intValue());

		select++;
		assertStatements();

		amusementParkDetailResponseDto = amusementParkService.findDetailById(101L);

		assertEquals("test park 101", amusementParkDetailResponseDto.getName());
		assertEquals(0, amusementParkDetailResponseDto.getNumberOfMachines().intValue());
		assertEquals(0, amusementParkDetailResponseDto.getNumberOfGuestBookRegistries().intValue());
		assertEquals(0, amusementParkDetailResponseDto.getNumberOfActiveVisitors().intValue());
		assertEquals(0, amusementParkDetailResponseDto.getNumberOfKnownVisitors().intValue());

		select++;
		assertStatements();
	}

	@Test
	public void deleteTest() {
		amusementParkService.delete(101L);
		select += 5;
		delete++;
		assertStatements();
	}

	@Test
	public void findAllTest() {
		AmusementParkSearchRequestDto dto = new AmusementParkSearchRequestDto();
		dto.setName("test");

		Page<AmusementParkDetailResponseDto> page = amusementParkService.findAll(dto, PageRequest.of(0, 10));

		assertEquals(2, page.getTotalElements());

		select += 2;
		assertStatements();
	}

}