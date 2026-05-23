package hu.bence.amusementpark.test.integration.controller;

import hu.bence.amusementpark.dto.request.MachineCreateRequestDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
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


public class MachineControllerIntegrationTests extends AbstractApplicationTests {

    @Test
    public void visitorCanQueryMachinesButCanNotCreateTest() {
        login(VISITOR, VALID_PASSWORD);

        getMachinesWorks();

        postMachinesAccessDenied();
    }

    private void getMachinesWorks() {
        ResponseEntity<RestResponsePage<MachineSearchResponseDto>> response =
                restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
    }

    private void postMachinesAccessDenied() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> postMachine(ValidRequestDtoFactory.createMachine()));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access Denied", exception.getResponseBodyAsString());
    }

    private ResponseEntity<Void> postMachine(MachineCreateRequestDto dto) {
        return restTemplate.postForEntity(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines", dto, Void.class);
    }

    @Test
    @DirtiesContext
    public void createMachineValidationTest() {
        login(CREATOR, VALID_PASSWORD);

        MachineCreateRequestDto machineCreateRequestDto = ValidRequestDtoFactory.createMachine();
        machineCreateRequestDto.setFantasyName("Wron");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> postMachine(machineCreateRequestDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(validationError("fantasyName", sizeMessage(5, 50)), exception.getResponseBodyAsString());

        machineCreateRequestDto.setFantasyName("Titanic0");
        machineCreateRequestDto.setTicketPrice(0);

        exception = assertThrows(HttpClientErrorException.class,
                () -> postMachine(machineCreateRequestDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals(validationError("ticketPrice", rangeMessage(1, 30)), exception.getResponseBodyAsString());

        machineCreateRequestDto.setTicketPrice(1);

        ResponseEntity<Void> response = postMachine(machineCreateRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    public void adminCanCreateTest() {
        login(NAME, VALID_PASSWORD);

        MachineCreateRequestDto machineCreateRequestDto = ValidRequestDtoFactory.createMachine();
        machineCreateRequestDto.setFantasyName("Titanic0");

        restTemplate.postForEntity(getBaseUrl() + "amusement-parks/" + amusementParkIdBence + "/machines",
                machineCreateRequestDto, Void.class);
    }

    @Test
    @DirtiesContext
    public void pagedTest() {
        login(CREATOR, VALID_PASSWORD);

        ResponseEntity<RestResponsePage<MachineSearchResponseDto>> response =
                restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RestResponsePage<MachineSearchResponseDto> page = response.getBody();
        assertEquals(1, page.getTotalElements());
        assertEquals(1, page.getContent().size());

        IntStream.range(0, 11).forEach(i -> {
            MachineCreateRequestDto dto = ValidRequestDtoFactory.createMachine();
            dto.setFantasyName(dto.getFantasyName() + i);
            postMachine(dto);
        });

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(12, page.getTotalElements());
        assertEquals(10, page.getContent().size());
        response = restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines?page=1", HttpMethod.GET,
                HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(2, page.getContent().size());

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines?input=" +
                        encode("{\"fantasyName\":\"Big ship\"}") + "&page=1",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(11, page.getTotalElements());
        assertEquals(1, page.getContent().size());

        response = restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/machines?input=" + encode("{\"fantasyName\":\"x\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(0, page.getContent().size());
    }
}
