package hu.beni.amusementpark.test.integration;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.DataSourceInitializator;
import hu.beni.amusementpark.config.RestTemplateConfig;
import hu.beni.amusementpark.constants.StringParamConstants;
import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.dto.resource.MachineResource;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.amusementpark.helper.ValidResourceFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.*;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = {AmusementParkApplication.class,
        RestTemplateConfig.class, DataSourceInitializator.class})
public class AmusementParkApplicationTests {

    private static final TypeReferences.PagedModelType<EntityModel<AmusementParkResource>> PAGED_AMUSEMENT_PARK = new TypeReferences.PagedModelType<>() {
    };
    private static final TypeReferences.EntityModelType<AmusementParkResource> SINGLE_AMUSEMENT_PARK = new TypeReferences.EntityModelType<>() {
    };
    private static final TypeReferences.EntityModelType<VisitorResource> SINGLE_VISITOR = new TypeReferences.EntityModelType<>() {
    };
    private static final TypeReferences.EntityModelType<MachineResource> SINGLE_MACHINE = new TypeReferences.EntityModelType<>() {
    };
    private static final TypeReferences.EntityModelType<GuestBookRegistryResource> SINGLE_GUEST_BOOK_REGISTRY = new TypeReferences.EntityModelType<>() {
    };

    private static Map<String, String> links;

    static {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Autowired
    private RestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @PostConstruct
    public void init() {
        links = links == null ? getBaseLinks() : links;
    }

    private Map<String, String> getBaseLinks() {
        return Stream.of(restTemplate.getForObject("http://localhost:" + port + "/api/links", Link[].class))
                .collect(toMap(link -> link.getRel().value(), Link::getHref));
    }

    @Test
    public void signUpAndUploadMoneyAndVisitorCanNotCreateParkAndLogoutTest() {
        VisitorResource inputVisitorResource = ValidResourceFactory.createVisitor();

        EntityModel<VisitorResource> responseVisitorResource = signUp(inputVisitorResource);

        assertSignedUpVisitor(inputVisitorResource, responseVisitorResource, 250);

        uploadMoney500(responseVisitorResource.getLink(UPLOAD_MONEY).get().getHref());

        assertSignedUpVisitor(inputVisitorResource,
                restTemplate.exchange(responseVisitorResource.getLink(SELF).get().getHref(), HttpMethod.GET, HttpEntity.EMPTY, SINGLE_VISITOR).getBody(),
                750);

        getAmusementParksWorks();

        postAmusementParksAccessIsDenied();

        logout();
    }

    private EntityModel<VisitorResource> signUp(VisitorResource visitorResource) {
        return restTemplate.exchange(links.get(SIGN_UP), HttpMethod.POST, new HttpEntity<>(visitorResource), SINGLE_VISITOR).getBody();
    }

    private void assertSignedUpVisitor(VisitorResource inputVisitorResource, EntityModel<VisitorResource> actualVisitorResource,
                                       Integer spendingMoney) {
        assertEquals(inputVisitorResource.getEmail(), actualVisitorResource.getContent().getEmail());
        assertEquals(inputVisitorResource.getDateOfBirth(), actualVisitorResource.getContent().getDateOfBirth());
        assertEquals(spendingMoney.intValue(), actualVisitorResource.getContent().getSpendingMoney().intValue());
        assertEquals("ROLE_VISITOR", actualVisitorResource.getContent().getAuthority());
        assertNull(actualVisitorResource.getContent().getPassword());
        assertNull(actualVisitorResource.getContent().getConfirmPassword());
        assertEquals(3, actualVisitorResource.getLinks().stream().count());
        assertTrue(actualVisitorResource.getLink(SELF).get().getHref().endsWith("/me"));
        assertTrue(actualVisitorResource.getLink(UPLOAD_MONEY).isPresent());
        assertTrue(actualVisitorResource.getLink(AMUSEMENT_PARK).isPresent());
    }

    private void uploadMoney500(String uploadMoneyHref) {
        restTemplate.postForObject(uploadMoneyHref, 500, Void.class);
    }

    private void getAmusementParksWorks() {
        ResponseEntity<PagedModel<EntityModel<AmusementParkResource>>> response = getAmusementParks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getContent().size());
    }

    private ResponseEntity<PagedModel<EntityModel<AmusementParkResource>>> getAmusementParks() {
        return restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
    }

    private void postAmusementParksAccessIsDenied() {
        assertThrows(() -> restTemplate.postForEntity(links.get(AMUSEMENT_PARK),
                ValidResourceFactory.createAmusementPark(), Void.class), HttpClientErrorException.class, exception -> {
            assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
            assertEquals("Access Denied", exception.getResponseBodyAsString());
        });
    }

    private void logout() {
        ResponseEntity<String> response = restTemplate.postForEntity(links.get(LOGOUT), null, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("<title>Amusement Park</title>"));
    }

    @Test
    public void pageTest() {
        loginAsAdmin();

        ResponseEntity<PagedModel<EntityModel<AmusementParkResource>>> response = getAmusementParks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PagedModel<EntityModel<AmusementParkResource>> page = response.getBody();
        assertEquals(1, page.getLinks().stream().count());
        assertTrue(page.getLink(SELF).isPresent());

        IntStream.range(0, 11).forEach(i -> postAmusementPark());

        response = getAmusementParks();
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(4, page.getLinks().stream().count());
        assertTrue(page.getLink("first").isPresent());
        assertTrue(page.getLink("next").isPresent());
        assertTrue(page.getLink("last").isPresent());

        response = restTemplate.exchange(page.getLink("last").get().getHref(), HttpMethod.GET, HttpEntity.EMPTY,
                PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(4, page.getLinks().stream().count());
        assertTrue(page.getLink("first").isPresent());
        assertTrue(page.getLink("prev").isPresent());
        assertTrue(page.getLink("last").isPresent());

        response = restTemplate.exchange(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"a\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(4, page.getLinks().stream().count());
        assertTrue(page.getLink("last").isPresent());

        response = restTemplate.exchange(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"x\"}"),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        page = response.getBody();
        assertEquals(1, page.getLinks().stream().count());
    }

    private EntityModel<VisitorResource> loginAsAdmin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<EntityModel<VisitorResource>> response = restTemplate.exchange(links.get(LOGIN), HttpMethod.POST,
                new HttpEntity<>(createMap("nembence1994@gmail.com", StringParamConstants.VALID_PASSWORD), headers), SINGLE_VISITOR);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        log.info(response.getHeaders().toString());
        assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("SESSION="));

        EntityModel<VisitorResource> visitorResourceEntityModel = response.getBody();
        VisitorResource visitorResource = visitorResourceEntityModel.getContent();

        assertNotNull(visitorResource);
        log.info(visitorResource.toString());
        assertEquals(3, visitorResourceEntityModel.getLinks().stream().count());
        assertTrue(visitorResourceEntityModel.getLink(SELF).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(AMUSEMENT_PARK).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(UPLOAD_MONEY).isPresent());

        assertEquals("nembence1994@gmail.com", visitorResource.getEmail());
        assertEquals("ROLE_ADMIN", visitorResource.getAuthority());

        return visitorResourceEntityModel;
    }

    private MultiValueMap<String, String> createMap(String username, String password) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", username);
        map.add("password", password);
        return map;
    }

    private EntityModel<AmusementParkResource> postAmusementPark() {
        AmusementParkResource amusementParkResource = ValidResourceFactory.createAmusementPark();

        ResponseEntity<EntityModel<AmusementParkResource>> response =
                restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.POST,
                        new HttpEntity<>(amusementParkResource), SINGLE_AMUSEMENT_PARK);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EntityModel<AmusementParkResource> responseAmusementParkResource = response.getBody();

        assertNotNull(responseAmusementParkResource);
        assertEquals(4, responseAmusementParkResource.getLinks().stream().count());
        assertTrue(responseAmusementParkResource.getLink(SELF).get().getHref()
                .endsWith(responseAmusementParkResource.getContent().getIdentifier().toString()));
        assertTrue(responseAmusementParkResource.getLink(MACHINE).isPresent());
        assertTrue(responseAmusementParkResource.getLink(SIGN_UP).isPresent());
        assertTrue(responseAmusementParkResource.getLink(VISITOR_ENTER_PARK).isPresent());

        amusementParkResource.setIdentifier(responseAmusementParkResource.getContent().getIdentifier());
        assertEquals(amusementParkResource, responseAmusementParkResource.getContent());

        return responseAmusementParkResource;
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

        AmusementParkResource invalidAmusementParkResource = ValidResourceFactory.createAmusementPark();
        invalidAmusementParkResource.setEntranceFee(0);

        assertThrows(
                () -> restTemplate.postForObject(links.get(AMUSEMENT_PARK), invalidAmusementParkResource, Void.class),
                HttpClientErrorException.class, teaPotStatusAndEntranceFeeInvalidMessage());

        AmusementParkResource amusementParkResource = invalidAmusementParkResource;
        amusementParkResource.setEntranceFee(50);
        amusementParkResource.setCapital(500);
        ResponseEntity<EntityModel<AmusementParkResource>> response = restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.POST,
                new HttpEntity<>(amusementParkResource), SINGLE_AMUSEMENT_PARK);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String machineLinkHref = response.getBody().getLink(MACHINE).get().getHref();

        MachineResource machineResource = ValidResourceFactory.createMachine();
        machineResource.setType("asd");

        assertThrows(() -> restTemplate.postForObject(machineLinkHref, machineResource, Void.class),
                HttpClientErrorException.class, teaPotStatusAndMachineTypeMustMatch());

        machineResource.setType(MachineType.CAROUSEL.toString());
        machineResource.setPrice(2000);

        assertThrows(() -> restTemplate.postForObject(machineLinkHref, machineResource, Void.class),
                HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());
    }

    @Test
    public void positiveTest() {
        EntityModel<VisitorResource> visitorResource = loginAsAdmin();

        EntityModel<AmusementParkResource> amusementParkResourceEntityModel = postAmusementPark();

        EntityModel<MachineResource> machineResourceEntityModel = addMachine(amusementParkResourceEntityModel.getLink(MACHINE).get().getHref());

        visitorResource = enterPark(amusementParkResourceEntityModel.getLink(VISITOR_ENTER_PARK).get().getHref());

        visitorResource = getOnMachine(machineResourceEntityModel.getLink(GET_ON_MACHINE).get().getHref());

        visitorResource = getOffMachine(visitorResource.getLink(GET_OFF_MACHINE).get().getHref());

        addRegistry(visitorResource.getLink(ADD_REGISTRY).get().getHref());

        leavePark(visitorResource.getLink(VISITOR_LEAVE_PARK).get().getHref());

        deletePark(amusementParkResourceEntityModel.getLink(SELF).get().getHref());
    }

    private EntityModel<MachineResource> addMachine(String url) {
        ResponseEntity<EntityModel<MachineResource>> response = restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity<>(ValidResourceFactory.createMachine()), SINGLE_MACHINE);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EntityModel<MachineResource> machineResourceEntityModel = response.getBody();

        assertNotNull(machineResourceEntityModel);
        assertEquals(2, machineResourceEntityModel.getLinks().stream().count());
        String id = machineResourceEntityModel.getContent().getIdentifier().toString();
        assertTrue(machineResourceEntityModel.getLink(SELF).get().getHref().endsWith(id));
        assertTrue(machineResourceEntityModel.getLink(GET_ON_MACHINE).isPresent());

        return machineResourceEntityModel;
    }

    private EntityModel<VisitorResource> enterPark(String enterParkUrl) {
        ResponseEntity<EntityModel<VisitorResource>> response = restTemplate.exchange(enterParkUrl, HttpMethod.PUT, HttpEntity.EMPTY,
                SINGLE_VISITOR);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assert6LinkInParkVisitor(response.getBody());

        return response.getBody();
    }

    private void assert6LinkInParkVisitor(EntityModel<VisitorResource> visitorResourceEntityModel) {
        assertNotNull(visitorResourceEntityModel);
        assertEquals(6, visitorResourceEntityModel.getLinks().stream().count());
        assertTrue(visitorResourceEntityModel.getLink(SELF).get().getHref().endsWith(ME));
        assertTrue(visitorResourceEntityModel.getLink(VISITOR_LEAVE_PARK).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(GET_ON_MACHINE).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(ADD_REGISTRY).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(MACHINE).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(UPLOAD_MONEY).isPresent());
    }

    private EntityModel<VisitorResource> getOnMachine(String getOnMachineUrl) {
        ResponseEntity<EntityModel<VisitorResource>> response = restTemplate.exchange(getOnMachineUrl, HttpMethod.PUT,
                HttpEntity.EMPTY, SINGLE_VISITOR);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EntityModel<VisitorResource> visitorResourceEntityModel = response.getBody();

        assertNotNull(visitorResourceEntityModel);
        assertEquals(3, visitorResourceEntityModel.getLinks().stream().count());
        assertTrue(visitorResourceEntityModel.getLink(SELF).get().getHref().endsWith(ME));
        assertTrue(visitorResourceEntityModel.getLink(GET_OFF_MACHINE).isPresent());
        assertTrue(visitorResourceEntityModel.getLink(UPLOAD_MONEY).isPresent());

        return visitorResourceEntityModel;
    }

    private EntityModel<VisitorResource> getOffMachine(String getOffMachineUrl) {
        ResponseEntity<EntityModel<VisitorResource>> response = restTemplate.exchange(getOffMachineUrl, HttpMethod.PUT,
                HttpEntity.EMPTY, SINGLE_VISITOR);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assert6LinkInParkVisitor(response.getBody());

        return response.getBody();
    }

    private void addRegistry(String addRegistryUrl) {
        ResponseEntity<EntityModel<GuestBookRegistryResource>> response = restTemplate.exchange(addRegistryUrl, HttpMethod.POST,
                new HttpEntity<>(OPINION_ON_THE_PARK), SINGLE_GUEST_BOOK_REGISTRY);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        EntityModel<GuestBookRegistryResource> guestBookRegistryResource = response.getBody();

        assertNotNull(guestBookRegistryResource);
        assertEquals(2, guestBookRegistryResource.getLinks().stream().count());
        assertTrue(guestBookRegistryResource.getLink(SELF).isPresent());
        assertTrue(guestBookRegistryResource.getLink(ADD_REGISTRY).isPresent());
    }

    private void leavePark(String leaveParkUrl) {
        restTemplate.put(leaveParkUrl, null);
    }

    private void deletePark(String amusementParkUrlWithId) {
        restTemplate.delete(amusementParkUrlWithId);
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
