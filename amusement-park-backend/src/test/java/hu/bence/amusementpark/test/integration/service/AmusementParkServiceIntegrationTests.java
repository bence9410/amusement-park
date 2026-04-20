package hu.bence.amusementpark.test.integration.service;

import hu.bence.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.entity.AmusementPark;
import hu.bence.amusementpark.service.AmusementParkService;
import hu.bence.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static hu.bence.amusementpark.constants.StringParamConstants.NAME;
import static hu.bence.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AmusementParkServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private AmusementParkService amusementParkService;

    @Test
    public void saveTest() {
        AmusementPark amusementPark = createAmusementPark();

        amusementParkService.save(amusementPark, NAME);

        assertNotNull(amusementPark.getOwner());
        select++;
        insert++;
        assertStatements();
    }

    @Test
    public void findAllTest() {
        AmusementParkSearchRequestDto dto = new AmusementParkSearchRequestDto();
        dto.setName("park");
        dto.setMinEntranceFee(200);

        Page<AmusementParkSearchResponseDto> page = amusementParkService.findAll(dto, PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
        select += 2;
        assertStatements();
    }

}