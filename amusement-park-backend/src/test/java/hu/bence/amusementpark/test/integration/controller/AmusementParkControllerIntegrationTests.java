package hu.bence.amusementpark.test.integration.controller;

import hu.bence.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.helper.RestResponsePage;
import hu.bence.amusementpark.helper.ValidRequestDtoFactory;
import hu.bence.amusementpark.test.integration.AbstractApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;

import java.util.stream.IntStream;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.bence.amusementpark.constants.StringParamConstants.*;
import static hu.bence.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.bence.amusementpark.constants.ValidationMessageConstants.sizeMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AmusementParkControllerIntegrationTests extends AbstractApplicationTests {


    @Test
    public void visitorCanQueryAmusementParksButCanNotCreateTest() {
        login(VISITOR, VALID_PASSWORD);

        getAmusementParksWorks();

        postAmusementParksAccessDenied();
    }

    private void getAmusementParksWorks() {
        ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> response =
                restTemplate.exchange(getBaseUrl() + "amusement-parks",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getContent().size());
    }

    private void postAmusementParksAccessDenied() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> postAmusementPark(ValidRequestDtoFactory.createAmusementPark()));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access Denied", exception.getResponseBodyAsString());
    }

    private ResponseEntity<Void> postAmusementPark(AmusementParkCreateRequestDto dto) {
        ResponseEntity<Void> response = restTemplate.postForEntity(getBaseUrl() + "amusement-parks", dto, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response;
    }

    @Test
    @DirtiesContext
    public void createAmusementParkValidationTest() {
        login(CREATOR, VALID_PASSWORD);

        AmusementParkCreateRequestDto amusementParkCreateRequestDto = ValidRequestDtoFactory.createAmusementPark();
        amusementParkCreateRequestDto.setName("Wro");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> postAmusementPark(amusementParkCreateRequestDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(validationError("name", sizeMessage(5, 50)), exception.getResponseBodyAsString());

        amusementParkCreateRequestDto.setName("Bence's park0");
        amusementParkCreateRequestDto.setEntranceFee(0);

        exception = assertThrows(HttpClientErrorException.class,
                () -> postAmusementPark(amusementParkCreateRequestDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(validationError("entranceFee", rangeMessage(5, 200)), exception.getResponseBodyAsString());

        amusementParkCreateRequestDto.setEntranceFee(50);

        ResponseEntity<Void> response = postAmusementPark(amusementParkCreateRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void adminCanCreateTest() {
        login(NAME, VALID_PASSWORD);

        AmusementParkCreateRequestDto amusementParkCreateRequestDto = ValidRequestDtoFactory.createAmusementPark();
        amusementParkCreateRequestDto.setName("Bence's park0");

        postAmusementPark(amusementParkCreateRequestDto);
    }

    @Test
    @DirtiesContext
    public void pagedTest() {
        login(CREATOR, VALID_PASSWORD);

        ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> response =
                restTemplate.exchange(getBaseUrl() + "amusement-parks",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RestResponsePage<AmusementParkSearchResponseDto> page = response.getBody();
        assertEquals(3, page.getTotalElements());
        assertEquals(3, page.getContent().size());

        IntStream.range(0, 11).forEach(i -> {
            AmusementParkCreateRequestDto dto = ValidRequestDtoFactory.createAmusementPark();
            dto.setName(dto.getName() + i);
            postAmusementPark(dto);
        });

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(14, page.getTotalElements());
        assertEquals(10, page.getContent().size());
        response = restTemplate.exchange(getBaseUrl() + "amusement-parks?page=1", HttpMethod.GET,
                HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(4, page.getContent().size());

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks?input=" +
                        encode("{\"name\":\"Bence's\"}") + "&page=1",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(12, page.getTotalElements());
        assertEquals(2, page.getContent().size());

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks?input=" + encode("{\"name\":\"x\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(0, page.getContent().size());
    }
}
