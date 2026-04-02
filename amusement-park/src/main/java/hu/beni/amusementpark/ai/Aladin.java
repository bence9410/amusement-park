package hu.beni.amusementpark.ai;

import hu.beni.amusementpark.constants.ErrorMessageConstants;
import hu.beni.amusementpark.constants.HATEOASLinkRelConstants;
import hu.beni.amusementpark.dto.resource.GuestBookRegistryResource;
import hu.beni.amusementpark.dto.resource.VisitorResource;
import hu.beni.amusementpark.dto.response.AmusementParkDetailResponseDto;
import hu.beni.amusementpark.dto.response.MachineSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.beni.amusementpark.constants.HATEOASLinkRelConstants.*;

@Slf4j
@RequiredArgsConstructor
public class Aladin {

    public static final TypeReferences.PagedModelType<EntityModel<AmusementParkDetailResponseDto>> PAGED_AMUSEMENT_PARK = new TypeReferences.PagedModelType<>() {
    };
    public static final TypeReferences.PagedModelType<EntityModel<MachineSearchResponseDto>> PAGED_MACHINE = new TypeReferences.PagedModelType<>() {
    };
    private static final TypeReferences.EntityModelType<VisitorResource> SINGLE_VISITOR = new TypeReferences.EntityModelType<>() {
    };
    private static final TypeReferences.EntityModelType<GuestBookRegistryResource> SINGLE_GUEST_BOOK_REGISTRY = new TypeReferences.EntityModelType<>() {
    };

    private final RestTemplate restTemplate;

    private Random random = new Random();

    @Value("${server.port:8080}")
    private int port;

    private Map<String, String> links;

    @Scheduled(fixedRate = 25 * 60 * 1000)
    public void start() {
        links = links == null
                ? Stream.of(restTemplate.getForObject("http://localhost:" + port + "/api/links", Link[].class)).collect(
                Collectors.toMap(link -> link.getRel().value(), Link::getHref))
                : links;

        EntityModel<VisitorResource> visitorResourceEntityModel = loginAsAladin();

        upload1000MoneyIfSpendingMoneyLessThan1000(visitorResourceEntityModel);

        EntityModel<AmusementParkDetailResponseDto> amusementParkResourceEntityModel = findBencesPark();

        visitorResourceEntityModel = enterPark(amusementParkResourceEntityModel);

        List<EntityModel<MachineSearchResponseDto>> machineResources = new ArrayList<>(
                findMachines(amusementParkResourceEntityModel));

        IntStream.range(0, 7).map(i -> machineResources.size()).map(random::nextInt).mapToObj(machineResources::get)
                .forEach(this::getOnMachineWaitAndGetOffSkipIfNoFreeSeat);

        addGuestBookRegistry(visitorResourceEntityModel);

        leavePark(visitorResourceEntityModel);

        logout();
    }

    private EntityModel<VisitorResource> loginAsAladin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("email", "aladin@gmail.com");
        map.add("password", "Pass1234");
        return restTemplate.exchange(links.get(LOGIN), HttpMethod.POST, new HttpEntity<>(map, headers), SINGLE_VISITOR).getBody();
    }

    private void upload1000MoneyIfSpendingMoneyLessThan1000(EntityModel<VisitorResource> visitorResourceEntityModel) {
        if (visitorResourceEntityModel.getContent().getSpendingMoney() < 1000) {
            restTemplate.postForObject(visitorResourceEntityModel.getLink(HATEOASLinkRelConstants.UPLOAD_MONEY).get().getHref(),
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

    private EntityModel<VisitorResource> enterPark(EntityModel<AmusementParkDetailResponseDto> amusementParkResourceEntityModel) {
        return restTemplate
                .exchange(amusementParkResourceEntityModel.getLink(HATEOASLinkRelConstants.VISITOR_ENTER_PARK).get().getHref(),
                        HttpMethod.PUT, HttpEntity.EMPTY, SINGLE_VISITOR)
                .getBody();
    }

    private Collection<EntityModel<MachineSearchResponseDto>> findMachines(
            EntityModel<AmusementParkDetailResponseDto> amusementParkResourceEntityModel) {
        return restTemplate.exchange(amusementParkResourceEntityModel.getLink(HATEOASLinkRelConstants.MACHINE).get().getHref(),
                HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent();
    }

    private void getOnMachineWaitAndGetOffSkipIfNoFreeSeat(EntityModel<MachineSearchResponseDto> machineResource) {
        try {
            ResponseEntity<EntityModel<VisitorResource>> response = restTemplate.exchange(
                    machineResource.getLink(HATEOASLinkRelConstants.GET_ON_MACHINE).get().getHref(), HttpMethod.PUT,
                    HttpEntity.EMPTY, SINGLE_VISITOR);

            Thread.sleep(60000 + random.nextInt(120000));

            restTemplate.exchange(response.getBody().getLink(HATEOASLinkRelConstants.GET_OFF_MACHINE).get().getHref(),
                    HttpMethod.PUT, HttpEntity.EMPTY, SINGLE_VISITOR);
        } catch (Throwable t) {
            log.error(ErrorMessageConstants.ERROR, t);
        }
    }

    private void addGuestBookRegistry(EntityModel<VisitorResource> visitorResourceEntityModel) {
        restTemplate.exchange(visitorResourceEntityModel.getLink(HATEOASLinkRelConstants.ADD_REGISTRY).get().getHref(),
                HttpMethod.POST, new HttpEntity<>("Amazing"), SINGLE_GUEST_BOOK_REGISTRY);
    }

    private void leavePark(EntityModel<VisitorResource> visitorResourceEntityModel) {
        restTemplate.put(visitorResourceEntityModel.getLink(HATEOASLinkRelConstants.VISITOR_LEAVE_PARK).get().getHref(), null);
    }

    private void logout() {
        restTemplate.postForEntity(links.get(LOGOUT), null, Void.class);
    }

}
