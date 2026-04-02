package hu.beni.tester.service;

import hu.beni.tester.dto.DeleteTime;
import hu.beni.tester.dto.SumAndTime;
import hu.beni.tester.dto.VisitorStuffTime;
import hu.beni.tester.factory.ResourceFactory;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.resource.AmusementParkResource;
import hu.beni.tester.resource.MachineResource;
import hu.beni.tester.resource.VisitorResource;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.beni.tester.constants.Constants.*;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.VISITOR;
import static hu.beni.tester.constants.HATEOASLinkRelConstants.*;
import static java.util.stream.Collectors.toMap;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Async
@Service
@Scope(SCOPE_PROTOTYPE)
public class AsyncService {

    private static final TypeReferences.PagedModelType<EntityModel<AmusementParkResource>> PAGED_AMUSEMENT_PARK = new TypeReferences.PagedModelType<>() {
    };

    public static final TypeReferences.PagedModelType<EntityModel<MachineResource>> PAGED_MACHINE = new TypeReferences.PagedModelType<>() {
    };

    public static final TypeReferences.CollectionModelType<VisitorResource> LIST_VISITOR = new TypeReferences.CollectionModelType<>() {
    };

    private static final TypeReferences.EntityModelType<AmusementParkResource> SINGLE_AMUSEMENT_PARK = new TypeReferences.EntityModelType<>() {
    };

    private static final TypeReferences.EntityModelType<VisitorResource> SINGLE_VISITOR = new TypeReferences.EntityModelType<>() {
    };

    private final RestTemplate restTemplate;
    private final String email;
    private final ResourceFactory resourceFactory;
    private final ApplicationProperties properties;
    private final Map<String, String> links;
    private String uploadMoneyUrl;

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
                .collect(toMap(link -> link.getRel().value(), Link::getHref));
    }

    public CompletableFuture<Void> login() {
        restTemplate.postForEntity(links.get(LOGIN), createMapWithEmailAndPass(), Void.class);
        return CompletableFuture.completedFuture(null);
    }

    private MultiValueMap<String, String> createMapWithEmailAndPass() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(EMAIL, email);
        map.add(PASSWORD_FIELD, VALID_PASSWORD);
        return map;
    }

    public CompletableFuture<Void> signUp() {
        uploadMoneyUrl = restTemplate
                .exchange(links.get(SIGN_UP), HttpMethod.POST, new HttpEntity<>(resourceFactory.createVisitor(email)), SINGLE_VISITOR)
                .getBody().getLink(UPLOAD_MONEY).get().getHref();
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> uploadMoney() {
        restTemplate.postForEntity(uploadMoneyUrl, properties.getData().getVisitor().getSpendingMoney(), Void.class);
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
        boolean thereIsStillData = true;
        while (thereIsStillData) {
            long tenStart = now();
            thereIsStillData = getPageDeleteAllFalseIfNoMore(url);
            if (thereIsStillData) {
                tenTimes.add(millisFrom(tenStart));
            }
        }
    }

    private boolean getPageDeleteAllFalseIfNoMore(String url) {
        Collection<EntityModel<AmusementParkResource>> data = restTemplate
                .exchange(url, HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK).getBody().getContent();
        data.stream().map(apr -> apr.getLink(SELF).get().getHref()).forEach(restTemplate::delete);
        return !data.isEmpty();
    }

    public CompletableFuture<Long> createAmusementParksWithMachines() {
        long start = now();
        createAmusementParks().map(a -> a.getLink(MACHINE).get().getHref()).forEach(this::createMachines);
        return CompletableFuture.completedFuture(millisFrom(start));
    }

    private Stream<EntityModel<AmusementParkResource>> createAmusementParks() {
        return IntStream.range(0, properties.getNumberOf().getAmusementParksPerAdmin())
                .mapToObj(i -> restTemplate.exchange(links.get(AMUSEMENT_PARK), HttpMethod.POST,
                        new HttpEntity<>(resourceFactory.createAmusementPark()), SINGLE_AMUSEMENT_PARK).getBody());
    }

    private void createMachines(String machineUrl) {
        IntStream.range(0, properties.getNumberOf().getMachinesPerPark())
                .forEach(i -> restTemplate.postForEntity(machineUrl, resourceFactory.createMachine(), Void.class));
    }

    public CompletableFuture<SumAndTime> sumAmusementParksCapital() {
        long start = now();
        long sum = sumCapital(links.get(AMUSEMENT_PARK));
        return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
    }

    private long sumCapital(String url) {
        Optional<String> nextPageUrl = Optional.of(url);
        long sum = 0;
        while (nextPageUrl.isPresent()) {
            PagedModel<EntityModel<AmusementParkResource>> page = restTemplate
                    .exchange(nextPageUrl.get(), HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK).getBody();
            nextPageUrl = page.getNextLink().map(Link::getHref);
            sum += page.getContent().stream()
                    .map(EntityModel::getContent)
                    .mapToInt(AmusementParkResource::getCapital).sum();
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
            PagedModel<EntityModel<AmusementParkResource>> page = restTemplate
                    .exchange(nextPageUrl.get(), HttpMethod.GET, HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK).getBody();
            nextPageUrl = page.getNextLink().map(Link::getHref);
            visitEverythingInParks(page.getContent(), oneParkTimes);
            tenParkTimes.add(millisFrom(tenParkStart));
        }
    }

    private void visitEverythingInParks(Collection<EntityModel<AmusementParkResource>> amusementParkResources,
                                        List<Long> oneParkTimes) {
        amusementParkResources.stream().map(apr -> apr.getLink(VISITOR_ENTER_PARK).get().getHref())
                .forEach(enterParkUrl -> visitEverythingInAPark(enterParkUrl, oneParkTimes));
    }

    private void visitEverythingInAPark(String enterParkUrl, List<Long> oneParkTimes) {
        long startPark = now();
        EntityModel<VisitorResource> visitorResource = restTemplate
                .exchange(enterParkUrl, HttpMethod.PUT, HttpEntity.EMPTY, SINGLE_VISITOR).getBody();
        getMachinesAndGetOnAndOff(visitorResource.getLink(MACHINE).get().getHref());
        addRegistryAndLeave(visitorResource);
        oneParkTimes.add(millisFrom(startPark));
    }

    private void getMachinesAndGetOnAndOff(String machinesUrl) {
        restTemplate.exchange(machinesUrl, HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent()
                .stream().forEach(
                        machineResource -> getOnAndOffMachine(machineResource.getLink(GET_ON_MACHINE).get().getHref()));
    }

    private void getOnAndOffMachine(String getOnMachineUrl) {
        EntityModel<VisitorResource> onMachineVisitor = restTemplate
                .exchange(getOnMachineUrl, HttpMethod.PUT, HttpEntity.EMPTY, SINGLE_VISITOR).getBody();
        restTemplate.put(onMachineVisitor.getLink(GET_OFF_MACHINE).get().getHref(), HttpEntity.EMPTY);
    }

    private void addRegistryAndLeave(EntityModel<VisitorResource> visitorResourceEntityModel) {
        restTemplate.postForEntity(visitorResourceEntityModel.getLink(ADD_REGISTRY).get().getHref(), GUEST_BOOK_REGISTRY_TEXT,
                Void.class);
        restTemplate.put(visitorResourceEntityModel.getLink(VISITOR_LEAVE_PARK).get().getHref(), HttpEntity.EMPTY);
    }

    public CompletableFuture<SumAndTime> sumVisitorsSpendingMoney() {
        long start = now();
        long sum = restTemplate.exchange(links.get(VISITOR), HttpMethod.GET, HttpEntity.EMPTY, LIST_VISITOR).getBody()
                .getContent().stream().mapToInt(VisitorResource::getSpendingMoney).sum();
        return CompletableFuture.completedFuture(new SumAndTime(sum, millisFrom(start)));
    }

    public CompletableFuture<Void> deleteAllVisitor() {
        restTemplate.exchange(links.get(VISITOR), HttpMethod.GET, HttpEntity.EMPTY, LIST_VISITOR).getBody().getContent()
                .stream().map(v -> v.getLink(SELF).get().getHref()).forEach(restTemplate::delete);
        return CompletableFuture.completedFuture(null);
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private long millisFrom(long start) {
        return now() - start;
    }
}
