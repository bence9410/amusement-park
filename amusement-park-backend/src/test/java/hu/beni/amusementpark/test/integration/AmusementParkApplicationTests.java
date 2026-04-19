package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.DataSourceInitializator;
import hu.beni.amusementpark.config.RestTemplateConfig;
import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.dto.request.AmusementParkCreateRequestDto;
import hu.beni.amusementpark.dto.request.MachineCreateRequestDto;
import hu.beni.amusementpark.dto.request.VisitorSignUpRequestDto;
import hu.beni.amusementpark.dto.response.AmusementParkSearchResponseDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import hu.beni.amusementpark.dto.response.VisitorResponseDto;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.amusementpark.helper.RestResponsePage;
import hu.beni.amusementpark.helper.ValidRequestDtoFactory;
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
import java.util.stream.Stream;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static java.util.stream.Collectors.toSet;
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
        VisitorSignUpRequestDto visitorSignUpRequestDto = ValidRequestDtoFactory.createVisitor();

        VisitorResponseDto visitorResponseDto = signUp(visitorSignUpRequestDto);

        assertEquals(visitorSignUpRequestDto.getEmail(), visitorResponseDto.getEmail());
        assertEquals(visitorSignUpRequestDto.getPhoto(), visitorResponseDto.getPhoto());
        assertEquals(250, visitorResponseDto.getSpendingMoney());
        assertEquals("ROLE_VISITOR", visitorResponseDto.getAuthority());

        uploadMoney500();

        visitorResponseDto = restTemplate.exchange("http://localhost:" + port + "/api/me", HttpMethod.GET, HttpEntity.EMPTY, VisitorResponseDto.class).getBody();
        assertEquals(750, visitorResponseDto.getSpendingMoney());

        getAmusementParksWorks();

        postAmusementParksAccessIsDenied();

        logout();
    }

    private VisitorResponseDto signUp(VisitorSignUpRequestDto visitorSignUpRequestDto) {
        return restTemplate.exchange("http://localhost:" + port + "/api/signUp", HttpMethod.POST, new HttpEntity<>(visitorSignUpRequestDto), VisitorResponseDto.class).getBody();
    }

    private void uploadMoney500() {
        restTemplate.postForObject("http://localhost:" + port + "/api/visitors/uploadMoney", 500, Void.class);
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
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/api/logout", null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
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

    private VisitorResponseDto loginAsAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<VisitorResponseDto> response = restTemplate.exchange("http://localhost:" + port + "/api/login", HttpMethod.POST,
                new HttpEntity<>(createMap("nembence1994@gmail.com", StringParamConstants.VALID_PASSWORD), headers), VisitorResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("SESSION="));
        VisitorResponseDto visitorResponseDto = response.getBody();
        assertEquals("nembence1994@gmail.com", visitorResponseDto.getEmail());
        assertEquals("ROLE_ADMIN", visitorResponseDto.getAuthority());
        return visitorResponseDto;
    }

    private MultiValueMap<String, String> createMap(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", username);
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
        amusementParkCreateRequestDto.setCapital(500);
        ResponseEntity<Void> response = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks", HttpMethod.POST,
                new HttpEntity<>(amusementParkCreateRequestDto), Void.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AmusementParkSearchResponseDto amusementParkSearchResponseDto = getAmusementParks().getBody().getContent().getLast();
        String machineUrl = "http://localhost:" + port + "/api/amusement-parks/" + amusementParkSearchResponseDto.getId() + "/machines";
        MachineCreateRequestDto machineResource = ValidRequestDtoFactory.createMachine();
        machineResource.setType("asd");
        assertThrows(() -> restTemplate.postForObject(machineUrl, machineResource, Void.class),
                HttpClientErrorException.class, teaPotStatusAndMachineTypeMustMatch());

        machineResource.setType(MachineType.CAROUSEL.toString());
        machineResource.setPrice(2000);
        assertThrows(() -> restTemplate.postForObject(machineUrl, machineResource, Void.class),
                HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());
    }

    @Test
    public void positiveTest() {
        VisitorResponseDto visitorResponseDto = loginAsAdmin();

        postAmusementPark();

        Long amusementParkId = getAmusementParks().getBody().getContent().getFirst().getId();

        addMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/machines");

        enterPark("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/visitors/enter-park");

        Long machineId = restTemplate.exchange("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/machines",
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent().getFirst().getId();

        getOnMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/visitors/get-on-machine");

        getOffMachine("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/visitors/get-off-machine");

        addRegistry("http://localhost:" + port + "/api//amusement-parks/" + amusementParkId + "/visitors/guest-book-registries");

        leavePark("http://localhost:" + port + "/api/amusement-parks/" + amusementParkId + "/visitors/leave-park");
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

    private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTypeMustMatch() {
        return exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals(
                    validationError("type", oneOfMessage(
                            Stream.of(MachineType.values()).map(MachineType::toString).collect(toSet()).toString())),
                    exception.getResponseBodyAsString());
        };
    }

    private ExceptionAsserter<HttpClientErrorException> teaPotStatusAndMachineTooExpensiveMessage() {
        return exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals(MACHINE_IS_TOO_EXPENSIVE, exception.getResponseBodyAsString());
        };
    }
}
