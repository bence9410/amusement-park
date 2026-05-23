package hu.bence.amusementpark.test.integration.controller;

import hu.bence.amusementpark.dto.request.ModifyMoneyRequestDto;
import hu.bence.amusementpark.dto.request.UserSignUpRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
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
import org.springframework.web.client.HttpServerErrorException;

import static hu.bence.amusementpark.constants.StringParamConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerIntegrationTests extends AbstractApplicationTests {

    @Test
    @DirtiesContext
    public void signUpAndMeAndLogoutTest() {
        UserSignUpRequestDto userSignUpRequestDto = ValidRequestDtoFactory.createUser();

        UserResponseDto userResponseDto = restTemplate.exchange(getBaseUrl() + "sign-up",
                HttpMethod.POST, new HttpEntity<>(userSignUpRequestDto), UserResponseDto.class).getBody();

        assertEquals(userSignUpRequestDto.getName(), userResponseDto.getName());
        assertEquals(userSignUpRequestDto.getPhoto(), userResponseDto.getPhoto());
        assertEquals(0, userResponseDto.getMoney());
        assertEquals("ROLE_VISITOR", userResponseDto.getAuthority());

        userResponseDto = restTemplate.getForEntity(getBaseUrl() + "me", UserResponseDto.class).getBody();
        assertEquals(userSignUpRequestDto.getName(), userResponseDto.getName());
        assertEquals(userSignUpRequestDto.getPhoto(), userResponseDto.getPhoto());
        assertEquals(0, userResponseDto.getMoney());
        assertEquals("ROLE_VISITOR", userResponseDto.getAuthority());

        getAmusementParksWorks();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(getBaseUrl() + "logout",
                    null, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (HttpServerErrorException e) {
            //ignore exception for no static resource for redirect to /
        }
    }

    private void getAmusementParksWorks() {
        ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> response = restTemplate.exchange(getBaseUrl() + "amusement-parks",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getContent().size());
    }

    @Test
    @DirtiesContext
    public void visitorFlowTest() {
        login(VISITOR, VALID_PASSWORD);

        restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId + "/enter-park",
                HttpMethod.PUT, HttpEntity.EMPTY, Void.class);

        restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId +
                        "/machines/" + machineId + "/get-on-machine",
                HttpMethod.PUT, HttpEntity.EMPTY, Void.class);

        restTemplate.exchange(getBaseUrl() + "amusement-parks/" + amusementParkId +
                        "/machines/" + machineId + "/get-off-machine",
                HttpMethod.PUT, HttpEntity.EMPTY, Void.class);

        restTemplate.put(getBaseUrl() + "amusement-parks/" + amusementParkId + "/leave-park", null);
    }

    @Test
    public void visitorForbiddenAtAdminTest() {
        login(VISITOR, VALID_PASSWORD);

        callAdminEndpointsAndExpectForbiddenTest();
    }

    private void callAdminEndpointsAndExpectForbiddenTest() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.getForEntity(getBaseUrl() + "admin/users", Void.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access Denied", exception.getResponseBodyAsString());

        exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.patchForObject(getBaseUrl() + "admin/modify-money",
                        ModifyMoneyRequestDto.builder().userName(NAME).value(100).build(), Void.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access Denied", exception.getResponseBodyAsString());

        exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.patchForObject(getBaseUrl() + "admin/make-creator", NAME, Void.class));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals("Access Denied", exception.getResponseBodyAsString());
    }

    @Test
    public void creatorForbiddenAtAdminTest() {
        login(CREATOR, VALID_PASSWORD);

        callAdminEndpointsAndExpectForbiddenTest();
    }

    @Test
    @DirtiesContext
    public void adminEndpointsWorksTest() {
        login(NAME, VALID_PASSWORD);

        ResponseEntity<RestResponsePage<UserResponseDto>> response = restTemplate.exchange(
                getBaseUrl() + "admin/users", HttpMethod.GET, HttpEntity.EMPTY, PAGED_USER);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getTotalElements());

        restTemplate.exchange(getBaseUrl() + "admin/modify-money", HttpMethod.PATCH,
                new HttpEntity<>(ModifyMoneyRequestDto.builder().userName(NAME).value(-10).build()), Void.class);

        restTemplate.exchange(getBaseUrl() + "admin/make-creator", HttpMethod.PATCH,
                new HttpEntity<>(VISITOR), Void.class);
    }
}
