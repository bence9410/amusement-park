package hu.bence.amusementpark.test.integration.service;

import hu.bence.amusementpark.dto.request.GuestBookRegistrySearchRequestDto;
import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.bence.amusementpark.service.GuestBookRegistryService;
import hu.bence.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static hu.bence.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuestBookRegistryServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private GuestBookRegistryService guestBookService;

    @Test
    public void addRegistryTest() {
        guestBookService.addRegistry(amusementParkId, inParkVisitorName, OPINION_ON_THE_PARK);
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
