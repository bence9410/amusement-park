package hu.beni.tester.service;

import static hu.beni.tester.constants.Constants.EMAIL;
import static hu.beni.tester.constants.Constants.GUEST_BOOK_REGISTRY_TEXT;
import static hu.beni.tester.constants.Constants.LINKS_URL;
import static hu.beni.tester.constants.Constants.PASSWORD;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.ADD_REGISTRY;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.AMUSEMENT_PARK;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.GET_OFF_MACHINE;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.GET_ON_MACHINE;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.LOGIN;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.LOGOUT;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.MACHINE;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.SIGN_UP;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.UPLOAD_MONEY;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.VISITOR;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.VISITOR_ENTER_PARK;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.VISITOR_LEAVE_PARK;
import static java.util.stream.Collectors.toMap;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.TypeReferences.PagedResourcesType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.VisitorStuffTime;
import hu.beni.tester.factory.ResourceFactory;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.resource.AmusementParkResource;
import hu.beni.tester.resource.VisitorResource;

@Async
@Service
@Scope(SCOPE_PROTOTYPE)
public class AsyncService {

	public static final PagedResourcesType<ResourceSupport> PAGED_TYPE = new PagedResourcesType<ResourceSupport>() {
	};

	private static final Map<Class, PagedResourcesType> PAGED_TYPES;

	static {
		PAGED_TYPES = new HashMap<>();
		PAGED_TYPES.put(AmusementParkResource.class, new PagedResourcesType<AmusementParkResource>() {
		});

		PAGED_TYPES.put(VisitorResource.class, new PagedResourcesType<VisitorResource>() {
		});
	}

	public static final <T> PagedResourcesType<T> getPagedType(Class<T> clazz) {
		return PAGED_TYPES.get(clazz);
	}

	private final RestTemplate restTemplate;
	private final String email;
	private final ResourceFactory resourceFactory;
	private final ApplicationProperties properties;
	private final Map<String, String> links;

	public AsyncService(RestTemplate restTemplate, String email, ResourceFactory resourceFactory,
			ApplicationProperties properties) {
		this.restTemplate = restTemplate;
		this.email = email;
		this.resourceFactory = resourceFactory;
		this.properties = properties;
		links = getBaseLinks();
	}

	private Map<String, String> getBaseLinks() {
		return Stream.of(restTemplate.getForObject(LINKS_URL, Link[].class))
				.collect(toMap(Link::getRel, Link::getHref));
	}

	public CompletableFuture<Void> login() {
		restTemplate.postForEntity(links.get(LOGIN), createMapWithEmailAndPass(), Void.class);
		return CompletableFuture.completedFuture(null);
	}

	private MultiValueMap<String, String> createMapWithEmailAndPass() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add(EMAIL, email);
		map.add(PASSWORD, PASSWORD);
		return map;
	}

	public CompletableFuture<Void> signUp() {
		restTemplate.postForEntity(links.get(SIGN_UP), resourceFactory.createVisitor(email), Void.class);
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<Void> uploadMoney() {
		restTemplate.postForEntity(links.get(UPLOAD_MONEY), properties.getData().getVisitor().getSpendingMoney(),
				Void.class);
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<?> logout() {
		restTemplate.postForEntity(links.get(LOGOUT), null, null);
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<DeleteTime> deleteAllPark() {
		List<Long> tenParkTimes = new LinkedList<>();
		long start = now();
		deleteAllOnUrl(links.get(AMUSEMENT_PARK), tenParkTimes);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenParkTimes));
	}

	private void deleteAllOnUrl(String url, List<Long> tenTimes) {
		boolean thereIsStillData;
		do {
			long tenStart = now();
			thereIsStillData = getPageDeleteAllFalseIfNoMore(url);
			if (thereIsStillData) {
				tenTimes.add(millisFrom(tenStart));
			}
		} while (thereIsStillData);
	}

	private boolean getPageDeleteAllFalseIfNoMore(String url) {
		Collection<ResourceSupport> data = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, PAGED_TYPE)
				.getBody().getContent();
		data.stream().map(ResourceSupport::getId).map(Link::getHref).forEach(restTemplate::delete);
		return !data.isEmpty();
	}

	public CompletableFuture<Long> createAmusementParksWithMachines() {
		long start = now();
		createAmusementParks().map(this::mapToMachineLinkHref).forEach(this::createMachines);
		return CompletableFuture.completedFuture(millisFrom(start));
	}

	private Stream<AmusementParkResource> createAmusementParks() {
		return IntStream.range(0, properties.getNumberOf().getAmusementParksPerAdmin())
				.mapToObj(i -> restTemplate.postForObject(links.get(AMUSEMENT_PARK),
						resourceFactory.createAmusementPark(), AmusementParkResource.class));
	}

	private String mapToMachineLinkHref(AmusementParkResource amusementParkResource) {
		return amusementParkResource.getLink(MACHINE).getHref();
	}

	private void createMachines(String machineUrl) {
		IntStream.range(0, properties.getNumberOf().getMachinesPerPark())
				.forEach(i -> restTemplate.postForEntity(machineUrl, resourceFactory.createMachine(), Void.class));
	}

	public CompletableFuture<SumAndTime> sumAmusementParksCapital() {
		long start = now();
		long sum = sum(links.get(AMUSEMENT_PARK), AmusementParkResource.class, AmusementParkResource::getCapital);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	private <T> long sum(String url, Class<T> clazz, ToIntFunction<T> toIntFunction) {
		Optional<String> nextPageUrl = Optional.of(url);
		long sum = 0;
		while (nextPageUrl.isPresent()) {
			PagedResources<T> page = restTemplate
					.exchange(nextPageUrl.get(), HttpMethod.GET, HttpEntity.EMPTY, getPagedType(clazz)).getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref);
			sum += page.getContent().stream().mapToInt(toIntFunction).sum();
		}
		return sum;
	}

	public CompletableFuture<VisitorStuffTime> visitAllStuffInEveryPark() {
		List<Long> oneParkTimes = new LinkedList<>();
		List<Long> tenParkTimes = new LinkedList<>();
		long start = now();
		visitAllStuffInEveryPark(oneParkTimes, tenParkTimes);
		return CompletableFuture.completedFuture(new VisitorStuffTime(millisFrom(start), tenParkTimes, oneParkTimes));
	}

	private void visitAllStuffInEveryPark(List<Long> oneParkTimes, List<Long> tenParkTimes) {
		Optional<String> nextPageUrl = Optional.of(links.get(AMUSEMENT_PARK));
		while (nextPageUrl.isPresent()) {
			long tenParkStart = now();
			PagedResources<AmusementParkResource> page = restTemplate.exchange(nextPageUrl.get(), HttpMethod.GET,
					HttpEntity.EMPTY, getPagedType(AmusementParkResource.class)).getBody();
			nextPageUrl = Optional.ofNullable(page.getNextLink()).map(Link::getHref);
			visitEverythingInParks(page.getContent(), oneParkTimes);
			tenParkTimes.add(millisFrom(tenParkStart));
		}
	}

	private void visitEverythingInParks(Collection<AmusementParkResource> amusementParkResources,
			List<Long> oneParkTimes) {
		amusementParkResources.stream().map(apr -> apr.getLink(VISITOR_ENTER_PARK).getHref())
				.forEach(enterParkUrl -> visitEverythingInAPark(enterParkUrl, oneParkTimes));
	}

	private void visitEverythingInAPark(String enterParkUrl, List<Long> oneParkTimes) {
		long startPark = now();
		VisitorResource visitorResource = restTemplate
				.exchange(enterParkUrl, HttpMethod.PUT, HttpEntity.EMPTY, VisitorResource.class).getBody();
		getMachinesAndGetOnAndOff(visitorResource.getLink(MACHINE).getHref());
		addRegistryAndLeave(visitorResource);
		oneParkTimes.add(millisFrom(startPark));
	}

	private void getMachinesAndGetOnAndOff(String machinesUrl) {
		restTemplate.exchange(machinesUrl, HttpMethod.GET, HttpEntity.EMPTY, PAGED_TYPE).getBody().getContent().stream()
				.forEach(machineResource -> getOnAndOffMachine(machineResource.getLink(GET_ON_MACHINE).getHref()));
	}

	private void getOnAndOffMachine(String getOnMachineUrl) {
		VisitorResource onMachineVisitor = restTemplate
				.exchange(getOnMachineUrl, HttpMethod.PUT, HttpEntity.EMPTY, VisitorResource.class).getBody();
		restTemplate.put(onMachineVisitor.getLink(GET_OFF_MACHINE).getHref(), HttpEntity.EMPTY);
	}

	private void addRegistryAndLeave(VisitorResource visitorResource) {
		restTemplate.postForEntity(visitorResource.getLink(ADD_REGISTRY).getHref(), GUEST_BOOK_REGISTRY_TEXT,
				Void.class);
		restTemplate.put(visitorResource.getLink(VISITOR_LEAVE_PARK).getHref(), HttpEntity.EMPTY);
	}

	public CompletableFuture<SumAndTime> sumVisitorsSpendingMoney() {
		long start = now();
		long sum = sum(links.get(VISITOR), VisitorResource.class, VisitorResource::getSpendingMoney);
		return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
	}

	public CompletableFuture<DeleteTime> deleteAllVisitor() {
		List<Long> tenVisitorTimes = new LinkedList<>();
		long start = now();
		deleteAllOnUrl(links.get(VISITOR), tenVisitorTimes);
		return CompletableFuture.completedFuture(new DeleteTime(millisFrom(start), tenVisitorTimes));
	}

	private long now() {
		return System.currentTimeMillis();
	}

	private long millisFrom(long start) {
		return now() - start;
	}
}
