package hu.beni.amusementpark.ai;

import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.AMUSEMENT_PARK;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.LOGIN;
import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.LOGOUT;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.TypeReferences.PagedModelType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import hu.beni.amusementpark.constants.ErrorMessageConstants;
import hu.beni.amusementpark.constants.HATEOASLinkRelConstants;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Aladin {

	public static final PagedModelType<EntityModel<AmusementParkDetailResponseDto>> PAGED_AMUSEMENT_PARK = new PagedModelType<EntityModel<AmusementParkDetailResponseDto>>() {
	};

	public static final PagedModelType<EntityModel<MachineSearchResponseDto>> PAGED_MACHINE = new PagedModelType<EntityModel<MachineSearchResponseDto>>() {
	};

	private final RestTemplate restTemplate;

	private Random random = new Random();

	@Value("${server.port:8080}")
	private int port;

	private Map<String, String> links;

	@Scheduled(fixedRate = 25 * 60 * 1000)
	public void start() {
		links = links == null
				? Stream.of(restTemplate.getForObject("http://localhost:" + port + "/links", Link[].class)).collect(
						Collectors.toMap(link -> link.getRel().value(), Link::getHref))
				: links;

		VisitorResource visitorResource = loginAsAladin();

		upload1000MoneyIfSpendingMoneyLessThan1000(visitorResource);

		EntityModel<AmusementParkDetailResponseDto> amusementParkResource = findBencesPark();

		visitorResource = enterPark(amusementParkResource);

		List<EntityModel<MachineSearchResponseDto>> machineResources = new ArrayList<>(
				findMachines(amusementParkResource));

		IntStream.range(0, 7).map(i -> machineResources.size()).map(random::nextInt).mapToObj(machineResources::get)
				.forEach(this::getOnMachineWaitAndGetOffSkipIfNoFreeSeat);

		addGuestBookRegistry(visitorResource);

		leavePark(visitorResource);

		logout();
	}

	private VisitorResource loginAsAladin() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("email", "aladin@gmail.com");
		map.add("password", "password");
		return restTemplate.postForObject(links.get(LOGIN), map, VisitorResource.class);
	}

	private void upload1000MoneyIfSpendingMoneyLessThan1000(VisitorResource visitorResource) {
		if (visitorResource.getSpendingMoney() < 1000) {
			restTemplate.postForObject(visitorResource.getLink(HATEOASLinkRelConstants.UPLOAD_MONEY).get().getHref(),
					1000, Void.class);
		}
	}

	private EntityModel<AmusementParkDetailResponseDto> findBencesPark() {
		return restTemplate.exchange(
				links.get(AMUSEMENT_PARK) + "?input="
						+ URLEncoder.encode("{\"name\":\"Bence\"}", StandardCharsets.UTF_8),
				HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK).getBody().getContent().stream().findFirst()
				.get();
	}

	private VisitorResource enterPark(EntityModel<AmusementParkDetailResponseDto> amusementParkResource) {
		return restTemplate
				.exchange(amusementParkResource.getLink(HATEOASLinkRelConstants.VISITOR_ENTER_PARK).get().getHref(),
						HttpMethod.PUT, HttpEntity.EMPTY, VisitorResource.class)
				.getBody();
	}

	private Collection<EntityModel<MachineSearchResponseDto>> findMachines(
			EntityModel<AmusementParkDetailResponseDto> amusementParkResource) {
		return restTemplate.exchange(amusementParkResource.getLink(HATEOASLinkRelConstants.MACHINE).get().getHref(),
				HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent();
	}

	private void getOnMachineWaitAndGetOffSkipIfNoFreeSeat(EntityModel<MachineSearchResponseDto> machineResource) {
		try {
			ResponseEntity<VisitorResource> response = restTemplate.exchange(
					machineResource.getLink(HATEOASLinkRelConstants.GET_ON_MACHINE).get().getHref(), HttpMethod.PUT,
					HttpEntity.EMPTY, VisitorResource.class);

			Thread.sleep(60000 + random.nextInt(120000));

			restTemplate.exchange(response.getBody().getLink(HATEOASLinkRelConstants.GET_OFF_MACHINE).get().getHref(),
					HttpMethod.PUT, HttpEntity.EMPTY, VisitorResource.class);
		} catch (Throwable t) {
			log.error(ErrorMessageConstants.ERROR, t);
		}
	}

	private void addGuestBookRegistry(VisitorResource visitorResource) {
		restTemplate.postForEntity(visitorResource.getLink(HATEOASLinkRelConstants.ADD_REGISTRY).get().getHref(),
				"Amazing", GuestBookRegistryResource.class);
	}

	private void leavePark(VisitorResource visitorResource) {
		restTemplate.put(visitorResource.getLink(HATEOASLinkRelConstants.VISITOR_LEAVE_PARK).get().getHref(), null);
	}

	private void logout() {
		restTemplate.postForEntity(links.get(LOGOUT), null, Void.class);
	}

}
