package hu.beni.amusementpark.test.integration;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.MACHINE_IS_TOO_EXPENSIVE;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.validationError;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.ADD_REGISTRY;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.AMUSEMENT_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.GET_OFF_MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.GET_ON_MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.LOGIN;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.LOGOUT;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.MACHINE;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.ME;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.SELF;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.SIGN_UP;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.UPLOAD_MONEY;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.VISITOR_ENTER_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.VISITOR_LEAVE_PARK;
import static hu.beni.amusementpark.constants.StringParamConstants.OPINION_ON_THE_PARK;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.oneOfMessage;
import static hu.beni.amusementpark.constants.ValidationMessageConstants.rangeMessage;
import static hu.beni.amusementpark.helper.MyAssert.assertThrows;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.TypeReferences.PagedModelType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { AmusementParkApplication.class,
		RestTemplateConfig.class, DataSourceInitializator.class })
public class AmusementParkApplicationTests {

	public static final PagedModelType<AmusementParkResource> PAGED_AMUSEMENT_PARK = new PagedModelType<AmusementParkResource>() {
	};

	static {
		Locale.setDefault(Locale.ENGLISH);
	}

	private static Map<String, String> links;

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

		VisitorResource responseVisitorResource = signUp(inputVisitorResource);

		assertSignedUpVisitor(inputVisitorResource, responseVisitorResource, 250);

		uploadMoney500(responseVisitorResource.getLink(UPLOAD_MONEY).get().getHref());

		assertSignedUpVisitor(inputVisitorResource,
				restTemplate.getForObject(responseVisitorResource.getLink(SELF).get().getHref(), VisitorResource.class),
				750);

		getAmusementParksWorks();

		postAmusementParksAccessIsDenied();

		logout();
	}

	private VisitorResource signUp(VisitorResource visitorResource) {
		return restTemplate.postForObject(links.get(SIGN_UP), visitorResource, VisitorResource.class);
	}

	private void assertSignedUpVisitor(VisitorResource inputVisitorResource, VisitorResource actualVisitorResource,
			Integer spendingMoney) {
		assertEquals(inputVisitorResource.getEmail(), actualVisitorResource.getEmail());
		assertEquals(inputVisitorResource.getDateOfBirth(), actualVisitorResource.getDateOfBirth());
		assertEquals(spendingMoney.intValue(), actualVisitorResource.getSpendingMoney().intValue());
		assertEquals("ROLE_VISITOR", actualVisitorResource.getAuthority());
		assertNull(actualVisitorResource.getPassword());
		assertNull(actualVisitorResource.getConfirmPassword());
		assertEquals(3, actualVisitorResource.getLinks().stream().count());
		assertTrue(actualVisitorResource.getLink(SELF).get().getHref().endsWith("/me"));
		assertTrue(actualVisitorResource.getLink(UPLOAD_MONEY).isPresent());
		assertTrue(actualVisitorResource.getLink(AMUSEMENT_PARK).isPresent());
	}

	private void uploadMoney500(String uploadMoneyHref) {
		restTemplate.postForObject(uploadMoneyHref, 500, Void.class);
	}

	private void getAmusementParksWorks() {
		ResponseEntity<PagedModel<AmusementParkResource>> response = getAmusementParks();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(3, response.getBody().getContent().size());
	}

	private ResponseEntity<PagedModel<AmusementParkResource>> getAmusementParks() {
		return restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
	}

	private void postAmusementParksAccessIsDenied() {
		assertThrows(() -> restTemplate.postForEntity(links.get(AMUSEMENT_PARK),
				ValidResourceFactory.createAmusementPark(), Void.class), HttpClientErrorException.class, exception -> {
					assertEquals(HttpStatus.I_AM_A_TEAPOT, exception.getStatusCode());
					assertEquals("Access is denied", exception.getResponseBodyAsString());
				});
	}

	private void logout() {
		ResponseEntity<Void> response = restTemplate.postForEntity(links.get(LOGOUT), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith(Integer.toString(port) + "/"));
	}

	@Test
	public void pageTest() {
		loginAsAdmin();

		ResponseEntity<PagedModel<AmusementParkResource>> response = getAmusementParks();
		assertEquals(HttpStatus.OK, response.getStatusCode());
		PagedModel<AmusementParkResource> page = response.getBody();
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

	private VisitorResource loginAsAdmin() {
		ResponseEntity<VisitorResource> response = restTemplate.postForEntity(links.get(LOGIN),
				createMap("nembence1994@gmail.com", StringParamConstants.VALID_PASSWORD), VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("SESSION="));

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		log.info(visitorResource.toString());
		assertEquals(3, visitorResource.getLinks().stream().count());
		assertTrue(visitorResource.getLink(SELF).isPresent());
		assertTrue(visitorResource.getLink(AMUSEMENT_PARK).isPresent());
		assertTrue(visitorResource.getLink(UPLOAD_MONEY).isPresent());

		assertEquals("nembence1994@gmail.com", visitorResource.getEmail());
		assertEquals("ROLE_ADMIN", visitorResource.getAuthority());

		return visitorResource;
	}

	private MultiValueMap<String, String> createMap(String username, String password) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("email", username);
		map.add("password", password);
		return map;
	}

	private AmusementParkResource postAmusementPark() {
		AmusementParkResource amusementParkResource = ValidResourceFactory.createAmusementPark();

		ResponseEntity<AmusementParkResource> response = restTemplate.postForEntity(links.get(AMUSEMENT_PARK),
				amusementParkResource, AmusementParkResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		AmusementParkResource responseAmusementParkResource = response.getBody();

		assertNotNull(responseAmusementParkResource);
		assertEquals(4, responseAmusementParkResource.getLinks().stream().count());
		assertTrue(responseAmusementParkResource.getLink(SELF).get().getHref()
				.endsWith(responseAmusementParkResource.getIdentifier().toString()));
		assertTrue(responseAmusementParkResource.getLink(MACHINE).isPresent());
		assertTrue(responseAmusementParkResource.getLink(SIGN_UP).isPresent());
		assertTrue(responseAmusementParkResource.getLink(VISITOR_ENTER_PARK).isPresent());

		amusementParkResource.setIdentifier(responseAmusementParkResource.getIdentifier());
		amusementParkResource.add(responseAmusementParkResource.getLinks());
		assertEquals(amusementParkResource, responseAmusementParkResource);

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
		ResponseEntity<AmusementParkResource> response = restTemplate.postForEntity(links.get(AMUSEMENT_PARK),
				amusementParkResource, AmusementParkResource.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		amusementParkResource = response.getBody();
		String machineLinkHref = amusementParkResource.getLink(MACHINE).get().getHref();

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
		VisitorResource visitorResource = loginAsAdmin();

		AmusementParkResource amusementParkResource = postAmusementPark();

		MachineResource machineResource = addMachine(amusementParkResource.getLink(MACHINE).get().getHref());

		visitorResource = enterPark(amusementParkResource.getLink(VISITOR_ENTER_PARK).get().getHref());

		visitorResource = getOnMachine(machineResource.getLink(GET_ON_MACHINE).get().getHref());

		visitorResource = getOffMachine(visitorResource.getLink(GET_OFF_MACHINE).get().getHref());

		addRegistry(visitorResource.getLink(ADD_REGISTRY).get().getHref());

		leavePark(visitorResource.getLink(VISITOR_LEAVE_PARK).get().getHref());

		deletePark(amusementParkResource.getLink(SELF).get().getHref());
	}

	private MachineResource addMachine(String url) {
		ResponseEntity<MachineResource> response = restTemplate.postForEntity(url, ValidResourceFactory.createMachine(),
				MachineResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		MachineResource machineResource = response.getBody();

		assertNotNull(machineResource);
		assertEquals(2, machineResource.getLinks().stream().count());
		assertTrue(machineResource.getLink(SELF).get().getHref().endsWith(machineResource.getIdentifier().toString()));
		assertTrue(machineResource.getLink(GET_ON_MACHINE).isPresent());

		return machineResource;
	}

	private VisitorResource enterPark(String enterParkUrl) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(enterParkUrl, HttpMethod.PUT, HttpEntity.EMPTY,
				VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assert6LinkInParkVisitor(visitorResource);

		return visitorResource;
	}

	private void assert6LinkInParkVisitor(VisitorResource visitorResource) {
		assertNotNull(visitorResource);
		assertEquals(6, visitorResource.getLinks().stream().count());
		assertTrue(visitorResource.getLink(SELF).get().getHref().endsWith(ME));
		assertTrue(visitorResource.getLink(VISITOR_LEAVE_PARK).isPresent());
		assertTrue(visitorResource.getLink(GET_ON_MACHINE).isPresent());
		assertTrue(visitorResource.getLink(ADD_REGISTRY).isPresent());
		assertTrue(visitorResource.getLink(MACHINE).isPresent());
		assertTrue(visitorResource.getLink(UPLOAD_MONEY).isPresent());
	}

	private VisitorResource getOnMachine(String getOnMachineUrl) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(getOnMachineUrl, HttpMethod.PUT,
				HttpEntity.EMPTY, VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(3, visitorResource.getLinks().stream().count());
		assertTrue(visitorResource.getLink(SELF).get().getHref().endsWith(ME));
		assertTrue(visitorResource.getLink(GET_OFF_MACHINE).isPresent());
		assertTrue(visitorResource.getLink(UPLOAD_MONEY).isPresent());

		return visitorResource;
	}

	private VisitorResource getOffMachine(String getOffMachineUrl) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(getOffMachineUrl, HttpMethod.PUT,
				HttpEntity.EMPTY, VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assert6LinkInParkVisitor(visitorResource);

		return visitorResource;
	}

	private void addRegistry(String addRegistryUrl) {
		ResponseEntity<GuestBookRegistryResource> response = restTemplate.postForEntity(addRegistryUrl,
				OPINION_ON_THE_PARK, GuestBookRegistryResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		GuestBookRegistryResource guestBookRegistryResource = response.getBody();

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
