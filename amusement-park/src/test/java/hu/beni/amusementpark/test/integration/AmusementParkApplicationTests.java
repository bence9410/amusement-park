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
import java.time.LocalDate;
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
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import hu.beni.amusementpark.AmusementParkApplication;
import hu.beni.amusementpark.config.ClientConfig;
import hu.beni.amusementpark.dto.resource.AmusementParkResource;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.dto.resource.MachineResource;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.enums.MachineType;
import hu.beni.amusementpark.exception.AmusementParkException;
import hu.beni.amusementpark.helper.MyAssert.ExceptionAsserter;
import hu.beni.amusementpark.helper.ValidResourceFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = { AmusementParkApplication.class,
		ClientConfig.class })
public class AmusementParkApplicationTests {

	public static final PagedResourcesType<AmusementParkResource> PAGED_AMUSEMENT_PARK = new PagedResourcesType<AmusementParkResource>() {
	};

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
		return Stream.of(restTemplate.getForObject("http://localhost:" + port + "/links", Link[].class))
				.collect(toMap(Link::getRel, Link::getHref));
	}

	@Test
	public void signUpAndUploadMoneyTest() {
		logout();

		VisitorResource inputVisitorResource = VisitorResource
				.builder() //@formatter:off
				.email("benike@gmail.com")
				.password("password")
				.confirmPassword("password")
				.dateOfBirth(LocalDate.of(1994, 10, 22)).build(); //@formatter:on

		VisitorResource responseVisitorResource = restTemplate.postForObject(links.get(SIGN_UP), inputVisitorResource,
				VisitorResource.class);

		assertVisitorResource(inputVisitorResource.getEmail(), inputVisitorResource.getDateOfBirth(), 250,
				responseVisitorResource);

		restTemplate.postForObject(links.get(UPLOAD_MONEY), 500, Void.class);

		assertVisitorResource(inputVisitorResource.getEmail(), inputVisitorResource.getDateOfBirth(), 750,
				restTemplate.getForObject(links.get(ME), VisitorResource.class));
	}

	private void assertVisitorResource(String email, LocalDate dateOfBirth, Integer spendingMoney,
			VisitorResource actualVisitorResource) {
		assertEquals(email, actualVisitorResource.getEmail());
		assertEquals(dateOfBirth, actualVisitorResource.getDateOfBirth());
		assertEquals(spendingMoney.intValue(), actualVisitorResource.getSpendingMoney().intValue());
		assertEquals("ROLE_VISITOR", actualVisitorResource.getAuthority());
		assertNull(actualVisitorResource.getPassword());
		assertNull(actualVisitorResource.getConfirmPassword());
		assertEquals(3, actualVisitorResource.getLinks().size());
		assertNotNull(actualVisitorResource.getId().getHref());
		assertNotNull(actualVisitorResource.getLink(VISITOR_ENTER_PARK).getHref());
		assertNotNull(actualVisitorResource.getLink(AMUSEMENT_PARK).getHref());
	}

	@Test
	public void pageTest() {
		loginAsAdmin("bence@gmail.com", "password");

		ResponseEntity<PagedResources<AmusementParkResource>> response = restTemplate
				.exchange(links.get(AMUSEMENT_PARK), HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		PagedResources<AmusementParkResource> page = response.getBody();
		assertEquals(1, page.getLinks().size());
		assertNotNull(page.getId());

		IntStream.range(0, 11).forEach(i -> postAmusementPark());

		response = restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.GET, HttpEntity.EMPTY,
				PAGED_AMUSEMENT_PARK);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
		assertNotNull(page.getLink("last"));

		response = restTemplate.exchange(page.getLink("last").getHref(), HttpMethod.GET, HttpEntity.EMPTY,
				PAGED_AMUSEMENT_PARK);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());

		response = restTemplate.exchange(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"a\"}"),
				HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(4, page.getLinks().size());
		assertNotNull(page.getLink("last"));

		response = restTemplate.exchange(links.get(AMUSEMENT_PARK) + "?input=" + encode("{\"name\":\"x\"}"),
				HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		page = response.getBody();
		assertEquals(1, page.getLinks().size());

	}

	@Test
	public void positiveTest() {
		VisitorResource visitorResource = loginAsAdmin("bence@gmail.com", "password");

		AmusementParkResource amusementParkResource = postAmusementPark();

		MachineResource machineResource = addMachine(amusementParkResource.getLink(MACHINE).getHref());

		visitorResource = enterPark(visitorResource.getLink(VISITOR_ENTER_PARK).getHref(),
				amusementParkResource.getIdentifier());

		visitorResource = getOnMachine(machineResource.getLink(GET_ON_MACHINE).getHref());

		visitorResource = getOffMachine(visitorResource.getLink(GET_OFF_MACHINE).getHref());

		addRegistry(visitorResource.getLink(ADD_REGISTRY).getHref());

		leavePark(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref());

		sellMachine(machineResource.getId().getHref());

		deletePark(amusementParkResource.getId().getHref());
	}

	@Test
	public void negativeTest() {
		loginAsAdmin("bence@gmail.com", "password");

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
		String machineLinkHref = amusementParkResource.getLink(MACHINE).getHref();

		MachineResource machineResource = ValidResourceFactory.createMachine();
		machineResource.setType("asd");

		assertThrows(() -> restTemplate.postForObject(machineLinkHref, machineResource, Void.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTypeMustMatch());

		machineResource.setType(MachineType.CAROUSEL.toString());
		machineResource.setPrice(2000);

		assertThrows(() -> restTemplate.postForObject(machineLinkHref, machineResource, Void.class),
				HttpClientErrorException.class, teaPotStatusAndMachineTooExpensiveMessage());
	}

	private VisitorResource loginAsAdmin(String email, String password) {
		logout();
		ResponseEntity<VisitorResource> response = restTemplate.postForEntity(links.get(LOGIN),
				createMap(email, password), VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getFirst("Set-Cookie").contains("JSESSIONID="));

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(3, visitorResource.getLinks().size());
		assertNotNull(visitorResource.getId().getHref());
		assertNotNull(visitorResource.getLink(VISITOR_ENTER_PARK));
		assertNotNull(visitorResource.getLink(AMUSEMENT_PARK));

		assertEquals(email, visitorResource.getEmail());
		assertEquals("ROLE_ADMIN", visitorResource.getAuthority());

		return visitorResource;
	}

	private MultiValueMap<String, String> createMap(String username, String password) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("email", username);
		map.add("password", password);
		return map;
	}

	private void logout() {
		ResponseEntity<Void> response = restTemplate.postForEntity(links.get(LOGOUT), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith(Integer.toString(port) + "/"));

		testRedirectToLoginPage();

		restTemplateFollowsRedirectOnGet();
	}

	private void testRedirectToLoginPage() {
		ResponseEntity<Void> response = restTemplate.postForEntity(links.get(AMUSEMENT_PARK), null, Void.class);

		assertEquals(HttpStatus.FOUND, response.getStatusCode());
		assertTrue(response.getHeaders().getLocation().toString().endsWith(Integer.toString(port) + "/"));
	}

	private void restTemplateFollowsRedirectOnGet() {
		ResponseEntity<String> loginPageResponse = restTemplate.getForEntity(links.get(AMUSEMENT_PARK), String.class);

		assertEquals(HttpStatus.OK, loginPageResponse.getStatusCode());
		assertTrue(loginPageResponse.getBody().length() > 450);
	}

	private AmusementParkResource postAmusementPark() {
		AmusementParkResource amusementParkResource = ValidResourceFactory.createAmusementPark();

		ResponseEntity<AmusementParkResource> response = restTemplate.postForEntity(links.get(AMUSEMENT_PARK),
				amusementParkResource, AmusementParkResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		AmusementParkResource responseAmusementParkResource = response.getBody();

		assertNotNull(responseAmusementParkResource);
		assertEquals(4, responseAmusementParkResource.getLinks().size());
		assertTrue(responseAmusementParkResource.getId().getHref()
				.endsWith(responseAmusementParkResource.getIdentifier().toString()));
		assertNotNull(responseAmusementParkResource.getLink(MACHINE));
		assertNotNull(responseAmusementParkResource.getLink(SIGN_UP));
		assertNotNull(responseAmusementParkResource.getLink(VISITOR_ENTER_PARK));

		amusementParkResource.setIdentifier(responseAmusementParkResource.getIdentifier());
		amusementParkResource.add(responseAmusementParkResource.getLinks());
		assertEquals(amusementParkResource, responseAmusementParkResource);

		return responseAmusementParkResource;
	}

	private MachineResource addMachine(String url) {
		ResponseEntity<MachineResource> response = restTemplate.postForEntity(url, ValidResourceFactory.createMachine(),
				MachineResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		MachineResource machineResource = response.getBody();

		assertNotNull(machineResource);
		assertEquals(2, machineResource.getLinks().size());
		assertTrue(machineResource.getId().getHref().endsWith(machineResource.getIdentifier().toString()));
		assertNotNull(machineResource.getLink(GET_ON_MACHINE));

		return machineResource;
	}

	private VisitorResource enterPark(String enterParkUrl, Long amusementParkId) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(
				UriComponentsBuilder.fromHttpUrl(enterParkUrl).build(amusementParkId), HttpMethod.PUT, HttpEntity.EMPTY,
				VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(5, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));
		assertNotNull(visitorResource.getLink(MACHINE));

		return visitorResource;
	}

	private VisitorResource getOnMachine(String getOnMachineUrl) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(getOnMachineUrl, HttpMethod.PUT,
				HttpEntity.EMPTY, VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(2, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(GET_OFF_MACHINE));

		return visitorResource;
	}

	private VisitorResource getOffMachine(String getOffMachineUrl) {
		ResponseEntity<VisitorResource> response = restTemplate.exchange(getOffMachineUrl, HttpMethod.PUT,
				HttpEntity.EMPTY, VisitorResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		VisitorResource visitorResource = response.getBody();

		assertNotNull(visitorResource);
		assertEquals(5, visitorResource.getLinks().size());
		assertTrue(visitorResource.getId().getHref().endsWith(ME));
		assertNotNull(visitorResource.getLink(VISITOR_LEAVE_PARK));
		assertNotNull(visitorResource.getLink(GET_ON_MACHINE));
		assertNotNull(visitorResource.getLink(ADD_REGISTRY));
		assertNotNull(visitorResource.getLink(MACHINE));

		return visitorResource;
	}

	private void addRegistry(String addRegistryUrl) {
		ResponseEntity<GuestBookRegistryResource> response = restTemplate.postForEntity(addRegistryUrl,
				OPINION_ON_THE_PARK, GuestBookRegistryResource.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		GuestBookRegistryResource guestBookRegistryResource = response.getBody();

		assertNotNull(guestBookRegistryResource);
		assertEquals(2, guestBookRegistryResource.getLinks().size());
		assertTrue(guestBookRegistryResource.getId().getHref()
				.endsWith(guestBookRegistryResource.getIdentifier().toString()));
		assertNotNull(guestBookRegistryResource.getLink(ADD_REGISTRY));
	}

	private void leavePark(String leaveParkUrl) {
		restTemplate.put(leaveParkUrl, null);
	}

	private void sellMachine(String machineUrlWithId) {
		restTemplate.delete(machineUrlWithId);
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

	private String encode(String input) {
		try {
			return URLEncoder.encode(input, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new AmusementParkException("Wrong input!", e);
		}
	}
}
