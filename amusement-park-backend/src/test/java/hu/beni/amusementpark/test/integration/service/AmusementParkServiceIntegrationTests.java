package hu.beni.amusementpark.test.integration.service;

import hu.beni.amusementpark.dto.request.AmusementParkSearchRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.service.AmusementParkService;
import hu.beni.amusementpark.test.integration.AbstractStatementCounterTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static hu.beni.amusementpark.helper.ValidEntityFactory.createAmusementPark;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AmusementParkServiceIntegrationTests extends AbstractStatementCounterTests {

    @Autowired
    private AmusementParkService amusementParkService;

    @Test
    public void saveTest() {
        amusementParkService.save(createAmusementPark());
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