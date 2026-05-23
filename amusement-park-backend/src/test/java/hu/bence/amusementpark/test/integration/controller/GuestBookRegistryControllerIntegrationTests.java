package hu.bence.amusementpark.test.integration.controller;

import hu.bence.amusementpark.dto.response.GuestBookRegistrySearchResponseDto;
import hu.bence.amusementpark.helper.RestResponsePage;
import hu.bence.amusementpark.test.integration.AbstractApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static hu.bence.amusementpark.constants.StringParamConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GuestBookRegistryControllerIntegrationTests extends AbstractApplicationTests {

    @Test
    @DirtiesContext
    public void addRegistryTest() {
        login(VISITOR, VALID_PASSWORD);

        restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/enter-park",
                HttpMethod.PUT, HttpEntity.EMPTY, Void.class);

        ResponseEntity<Void> response = restTemplate.postForEntity(
                getBaseUrl() + "amusement-parks/" + amusementParkId + "/guest-book-registries",
                OPINION_ON_THE_PARK, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void findAllTest() {
        login(VISITOR, VALID_PASSWORD);

        ResponseEntity<RestResponsePage<GuestBookRegistrySearchResponseDto>> response = restTemplate.exchange(
                getBaseUrl() + "amusement-parks/" + amusementParkId + "/guest-book-registries?input="
                        + encode("{\"textOfRegistry\": \"Amaz\", \"maxDateOfRegistry\": \"2026-05-23T18:00\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_GUEST_BOOK_REGISTRY);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
    }

}
