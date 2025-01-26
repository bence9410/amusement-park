package hu.beni.amusementpark.test.integration.service;

import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import hu.beni.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.beni.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.beni.amusementpark.entity.GuestBookRegistry;
import hu.beni.amusementpark.service.GuestBookRegistryService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;

public class GuestBookServiceIntegrationTests extends AbstractStatementCounterTests {

	@Autowired
	private GuestBookRegistryService guestBookService;

	@Test
	public void findByIdTest() {
		GuestBookRegistry guestBookRegistry = guestBookService.findById(guestBookId);

		assertEquals("test Amazing.", guestBookRegistry.getTextOfRegistry());
		assertEquals(amusementParkId, guestBookRegistry.getAmusementPark().getId().longValue());
		assertEquals(inParkVisitorEmail, guestBookRegistry.getVisitor().getEmail());

		select++;
		assertStatements();
	}

	@Test
	public void addRegistryTest() {
		assertNotNull(guestBookService.addRegistry(amusementParkId, inParkVisitorEmail, OPINION_ON_THE_PARK).getId());
		select += 2;
		insert++;
		assertStatements();
	}

	@Test
	public void findAllTest() {
		GuestBookRegistrySearchRequestDto dto = new GuestBookRegistrySearchRequestDto();
		dto.setAmusementParkId(amusementParkId);

		Page<GuestBookRegistrySearchResponseDto> page = guestBookService.findAll(dto, PageRequest.of(0, 10));

		assertEquals(2, page.getTotalElements());

		select += 2;
		assertStatements();
	}

}
