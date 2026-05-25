package hu.beni.tester.service;

import hu.beni.tester.dto.*;
import hu.beni.tester.factory.DtoFactory;
import hu.beni.tester.properties.ApplicationProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.beni.tester.constants.Constants.*;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Async
@Service
@Scope(SCOPE_PROTOTYPE)
public class AsyncService {

    private static final ParameterizedTypeReference<RestResponsePage<AmusementParkDto>> PAGED_AMUSEMENT_PARK = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<RestResponsePage<MachineDto>> PAGED_MACHINE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<RestResponsePage<UserDto>> PAGED_USER = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final String name;
    private final DtoFactory dtoFactory;
    private final ApplicationProperties properties;

    public AsyncService(RestTemplate restTemplate, String name, DtoFactory dtoFactory,
                        ApplicationProperties properties) {
        this.restTemplate = restTemplate;
        this.name = name;
        this.dtoFactory = dtoFactory;
        this.properties = properties;
    }

    public CompletableFuture<Void> login() {
        restTemplate.postForEntity(BASE_URL + "login", createMapWithEmailAndPass(), Void.class);
        return CompletableFuture.completedFuture(null);
    }

    private MultiValueMap<String, String> createMapWithEmailAndPass() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(NAME, name);
        map.add(PASSWORD_FIELD, VALID_PASSWORD);
        return map;
    }

    public CompletableFuture<Void> signUp() {
        restTemplate.exchange(BASE_URL + "sign-up", HttpMethod.POST,
                new HttpEntity<>(dtoFactory.createVisitor(name)), Void.class);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<?> logout() {
        restTemplate.postForEntity(BASE_URL + "logout", null, Void.class);
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Void> createAmusementParksWithMachines() {
        createAmusementParks().map(AmusementParkDto::getId).forEach(this::createMachines);
        return CompletableFuture.completedFuture(null);
    }

    private Stream<AmusementParkDto> createAmusementParks() {
        return IntStream.range(0, properties.getNumberOf().getAmusementParksPerAdmin())
                .mapToObj(i -> {
                    AmusementParkDto amusementParkDto = dtoFactory.createAmusementPark();
                    amusementParkDto.setName(amusementParkDto.getName() + name + i);
                    return restTemplate.exchange(BASE_URL + "amusement-parks", HttpMethod.POST,
                            new HttpEntity<>(amusementParkDto), AmusementParkDto.class).getBody();
                });
    }

    public CompletableFuture giveMoneyToEveryVisitor() {
        restTemplate.exchange(BASE_URL + "admin/users?size=100", HttpMethod.GET, HttpEntity.EMPTY, PAGED_USER)
                .getBody().stream().filter(user -> user.getAuthority().equals("ROLE_VISITOR"))
                .forEach(user -> restTemplate.patchForObject(BASE_URL + "admin/modify-money",
                        ModifyMoneyRequestDto.builder().userName(user.getName()).value(50000).build(), Void.class));
        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Integer> sumUsersMoney() {
        return CompletableFuture.completedFuture(restTemplate.exchange(BASE_URL + "admin/users?size=100",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_USER)
                .getBody().stream().mapToInt(UserDto::getMoney).sum());
    }

    private void createMachines(Long amusementParkId) {
        IntStream.range(0, properties.getNumberOf().getMachinesPerPark())
                .forEach(i -> {
                    MachineDto machineDto = dtoFactory.createMachine();
                    machineDto.setFantasyName(machineDto.getFantasyName() + name + amusementParkId + i);
                    restTemplate.postForEntity(BASE_URL + "amusement-parks/" + amusementParkId + "/machines",
                            machineDto, Void.class);
                });
    }

    public CompletableFuture<VisitorStuffTime> visitAllStuffInEveryPark() {
        List<Long> oneParkTimes = new LinkedList<>();
        List<Long> tenParkTimes = new LinkedList<>();
        long start = now();
        visitAllStuffInEveryPark(oneParkTimes, tenParkTimes);
        return CompletableFuture.completedFuture(new VisitorStuffTime(millisFrom(start), tenParkTimes, oneParkTimes));
    }

    private void visitAllStuffInEveryPark(List<Long> oneParkTimes, List<Long> tenParkTimes) {
        for (int i = 0; true; i++) {
            long tenParkStart = now();
            RestResponsePage<AmusementParkDto> page = restTemplate
                    .exchange(BASE_URL + "amusement-parks?page=" + i, HttpMethod.GET,
                            HttpEntity.EMPTY, PAGED_AMUSEMENT_PARK).getBody();
            visitEverythingInParks(page.getContent(), oneParkTimes);
            tenParkTimes.add(millisFrom(tenParkStart));
            if (page.isLast()) {
                break;
            }
        }
    }

    private void visitEverythingInParks(List<AmusementParkDto> amusementParkPage,
                                        List<Long> oneParkTimes) {
        amusementParkPage.stream().map(AmusementParkDto::getId)
                .forEach(amusementParkId -> visitEverythingInAPark(amusementParkId, oneParkTimes));
    }

    private void visitEverythingInAPark(Long amusementParkId, List<Long> oneParkTimes) {
        long startPark = now();
        restTemplate.put(BASE_URL + "amusement-parks/" + amusementParkId + "/enter-park", HttpEntity.EMPTY);
        getMachinesAndGetOnAndOff(amusementParkId);
        addRegistryAndLeave(amusementParkId);
        oneParkTimes.add(millisFrom(startPark));
    }

    private void getMachinesAndGetOnAndOff(Long amusementParkId) {
        restTemplate.exchange(BASE_URL + "amusement-parks/" + amusementParkId + "/machines?size=100",
                        HttpMethod.GET, HttpEntity.EMPTY, PAGED_MACHINE).getBody().getContent()
                .stream().map(MachineDto::getId)
                .forEach(machineId -> getOnAndOffMachine(amusementParkId, machineId));
    }

    private void getOnAndOffMachine(Long amusementParkId, Long machineId) {
        restTemplate.put(BASE_URL + "amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/get-on-machine", HttpEntity.EMPTY);
        restTemplate.put(BASE_URL + "amusement-parks/" + amusementParkId +
                "/machines/" + machineId + "/get-off-machine", HttpEntity.EMPTY);
    }

    private void addRegistryAndLeave(Long amusementParkId) {
        restTemplate.postForEntity(BASE_URL + "/amusement-parks/" + amusementParkId + "/guest-book-registries",
                GUEST_BOOK_REGISTRY_TEXT, Void.class);
        restTemplate.put(BASE_URL + "amusement-parks/" + amusementParkId + "/leave-park", HttpEntity.EMPTY);
    }

    private long now() {
        return System.currentTimeMillis();
    }

    private long millisFrom(long start) {
        return now() - start;
    }
}
