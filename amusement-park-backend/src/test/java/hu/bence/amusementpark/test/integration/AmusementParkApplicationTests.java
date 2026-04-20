package hu.bence.amusementpark.test.integration;

import hu.bence.amusementpark.AmusementParkApplication;
import hu.bence.amusementpark.config.DataSourceInitializator;
import hu.bence.amusementpark.config.RestTemplateConfig;
import hu.bence.amusementpark.constants.StringParamConstants;
import hu.bence.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.bence.amusementpark.dto.request.MachineCreateRequestDto;
import hu.bence.amusementpark.dto.request.UserSignUpRequestDto;
import hu.bence.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.bence.amusementpark.dto.response.MachineSearchResponseDto;
import hu.bence.amusementpark.dto.response.UserResponseDto;
import hu.bence.amusementpark.exception.AmusementParkException;
import hu.bence.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.bence.amusementpark.helper.RestResponsePage;
import hu.bence.amusementpark.helper.ValidRequestDtoFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.IntStream;

import static hu.bence.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.bence.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.bence.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.bence.amusementpark.constants.ValidationMessageConstants.sizeMessage;
import static hu.bence.amusementpark.helper.MyAssert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {AmusementParkApplication.class,
        RestTemplateConfig.class, DataSourceInitializator.class})
public class AmusementParkApplicationTests {

    private static final ParameterizedTypeReference<RestResponsePage<AmusementParkSearchResponseDto>> PAGED_AMUSEMENT_PARK = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<RestResponsePage<MachineSearchResponseDto>> PAGED_MACHINE = new ParameterizedTypeReference<>() {
    };


    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void signUpAndUploadMoneyAndVisitorCanNotCreateParkAndLogoutTest() {
        UserSignUpRequestDto userSignUpRequestDto = ValidRequestDtoFactory.createUser();

        UserResponseDto userResponseDto = signUp(userSignUpRequestDto);

        assertEquals(userSignUpRequestDto.getName(), userResponseDto.getName());
        assertEquals(userSignUpRequestDto.getPhoto(), userResponseDto.getPhoto());
        assertEquals(250, userResponseDto.getMoney());
        assertEquals("ROLE_VISITOR", userResponseDto.getAuthority());

        uploadMoney500();

        userResponseDto = restTemplate.exchange("http://localhost:" + port + "/api/me", HttpMethod.GET, HttpEntity.EMPTY, UserResponseDto.class).getBody();
        assertEquals(750, userResponseDto.getMoney());

        getAmusementParksWorks();

        postAmusementParksAccessIsDenied();

        logout();
    }

    private UserResponseDto signUp(UserSignUpRequestDto userSignUpRequestDto) {
        return restTemplate.exchange("http://localhost:" + port + "/api/signUp", HttpMethod.POST, new HttpEntity<>(userSignUpRequestDto), UserResponseDto.class).getBody();
    }

    private void uploadMoney500() {
        restTemplate.postForObject("http://localhost:" + port + "/api/uploadMoney", 500, Void.class);
    }

    private void getAmusementParksWorks() {
        ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> response = getAmusementParks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getContent().size());
    }

    private ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> getAmusementParks() {
        return restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks", HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
    }

    private void postAmusementParksAccessIsDenied() {
        assertThrows(() -> restTemplate.postForEntity("http://localhost:" + port + "/api/amusement-parks",
                ValidRequestDtoFactory.createAmusementPark(), Void.class), HttpClientErrorException.class, exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals("Access Denied", exception.getResponseBodyAsString());
        });
    }

    private void logout() {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/logout", null, String.class);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } catch (HttpClientErrorException e) {
            //ignore exception for no static resource for redirect to /
        }
    }

    @Test
    public void pageTest() {
        loginAsAdmin();

        ResponseEntity<RestResponsePage<AmusementParkSearchResponseDto>> response = getAmusementParks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RestResponsePage<AmusementParkSearchResponseDto> page;

        IntStream.range(0, 11).forEach(i -> postAmusementPark());

        response = getAmusementParks();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(16, page.getTotalElements());
        assertEquals(10, page.getContent().size());
        response = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks?page=1", HttpMethod.GET,
                HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(6, page.getContent().size());

        response = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks?input=" + encode("{\"name\":\"a\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(10, page.getContent().size());

        response = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks?input=" + encode("{\"name\":\"x\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        page = response.getBody();
        assertEquals(0, page.getContent().size());
    }

    private UserResponseDto loginAsAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<UserResponseDto> response = restTemplate.exchange("http://localhost:" + port + "/api/login", HttpMethod.POST,
                new HttpEntity<>(createMap("Bence", StringParamConstants.VALID_PASSWORD), headers), UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("SESSION="));
        UserResponseDto userResponseDto = response.getBody();
        assertEquals("Bence", userResponseDto.getName());
        assertEquals("ROLE_ADMIN", userResponseDto.getAuthority());
        return userResponseDto;
    }

    private MultiValueMap<String, String> createMap(String name, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("name", name);
        map.add("password", password);
        return map;
    }

    private void postAmusementPark() {
        AmusementParkCreateRequestDto amusementParkCreateRequestDto = ValidRequestDtoFactory.createAmusementPark();

        ResponseEntity<Void> response =
                restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks", HttpMethod.POST,
                        new HttpEntity<>(amusementParkCreateRequestDto), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private String encode(String input) {
        try {
            return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new AmusementParkException("Wrong input!", e);
        }
    }

    @Test
    public void negativeTest() {
        loginAsAdmin();

        AmusementParkCreateRequestDto amusementParkCreateRequestDto = ValidRequestDtoFactory.createAmusementPark();
        amusementParkCreateRequestDto.setEntranceFee(0);

        assertThrows(
                () -> restTemplate.postForObject("http://localhost:" + port + "/api/amusement-parks", amusementParkCreateRequestDto, Void.class),
                HttpClientErrorException.class, teaPotStatusAndEntranceFeeInvalidMessage());

        amusementParkCreateRequestDto.setEntranceFee(50);
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks", HttpMethod.POST,
                new HttpEntity<>(amusementParkCreateRequestDto), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AmusementParkSearchResponseDto amusementParkSearchResponseDto = getAmusementParks().getBody().getContent().getLast();
        String machineUrl = "http://localhost:" + port + "/api/amusement-parks/" + amusementParkSearchResponseDto.getId() + "/machines";
        MachineCreateRequestDto machineCreateRequestDto = ValidRequestDtoFactory.createMachine();
        machineCreateRequestDto.setFantasyName("");
        assertThrows(() -> restTemplate.postForObject(machineUrl, machineCreateRequestDto, Void.class),
                HttpClientErrorException.class, exception -> {
                    assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
                    assertEquals(validationError("fantasyName", sizeMessage(5, 50)), exception.getResponseBodyAsString());
                });
    }

    @Test
    public void positiveTest() {
        UserResponseDto userResponseDto = loginAsAdmin();

        postAmusementPark();

        Long amusementParkId = getAmusementParks().getBody().getContent().getFirst().getId();

        addMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/machines");

        enterPark("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/enter-park");

        Long machineId = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/machines",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent().getFirst().getId();

        getOnMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/get-on-machine");

        getOffMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/get-off-machine");

        addRegistry("http://localhost:" + port + "/api//amusement-parks/" + amusementParkId + "/guest-book-registries");

        leavePark("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/leave-park");
    }

    private void addMachine(String url) {
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity<>(ValidRequestDtoFactory.createMachine()), Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void enterPark(String enterParkUrl) {
        ResponseEntity<Void> response = restTemplate.exchange(enterParkUrl, HttpMethod.PUT, HttpEntity.EMPTY,
                Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void getOnMachine(String getOnMachineUrl) {
        ResponseEntity<Void> response = restTemplate.exchange(getOnMachineUrl, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void getOffMachine(String getOffMachineUrl) {
        ResponseEntity<Void> response = restTemplate.exchange(getOffMachineUrl, HttpMethod.PUT,
                HttpEntity.EMPTY, Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void addRegistry(String addRegistryUrl) {
        ResponseEntity<Void> response = restTemplate.exchange(addRegistryUrl, HttpMethod.POST,
                new HttpEntity<>(OPINION_ON_THE_PARK), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void leavePark(String leaveParkUrl) {
        restTemplate.put(leaveParkUrl, null);
    }

    private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndEntranceFeeInvalidMessage() {
        return exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals(validationError("entranceFee", rangeMessage(5, 200)), exception.getResponseBodyAsString());
        };
    }
}
