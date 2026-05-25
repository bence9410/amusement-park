package hu.beni.tester;

import hu.beni.tester.dto.TimeTo;
import hu.beni.tester.output.ResultLogger;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.service.AsyncService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Slf4j
@SpringBootApplication
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class TesterApplicationTests {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    @Qualifier("admins")
    private List<AsyncService> admins;

    @Autowired
    @Qualifier("visitors")
    private List<AsyncService> visitors;

    private AsyncService admin;

    private TimeTo timeTo;

    @PostConstruct
    public void init() {
        admin = admins.get(0);
    }

    @Test
    public void test() {

        loginAndSignUp();

        createAmusementParksWithMachines();

        IntStream.range(0, properties.getNumberOf().getRuns()).forEach(i -> performanceTest());

        logout();
    }

    private void performanceTest() {
        admin.giveMoneyToEveryVisitor().join();

        timeTo = new TimeTo();
        long start = System.currentTimeMillis();

        int money = admin.sumUsersMoney().join();

        visitorsVisitAllStuffInEveryPark();

        if (money != admin.sumUsersMoney().join()) {
            throw new RuntimeException("Problem with money!");
        }

        timeTo.setFullRun(System.currentTimeMillis() - start);

        log();
    }

    private void loginAndSignUp() {
        log.info("login and sign up");
        executeAdminsAsyncAndJoin(AsyncService::login);
        executeVisitorsAsyncAndJoin(AsyncService::signUp);
    }

    private void createAmusementParksWithMachines() {
        log.info("createAmusementParksWithMachines");
        executeAdminsAsyncAndJoin(AsyncService::createAmusementParksWithMachines);
    }

    private void visitorsVisitAllStuffInEveryPark() {
        log.info("visitorsVisitAllStuffInEveryPark");
        List<Long> wholeTimes = new LinkedList<>();
        List<Long> tenParkTimes = new LinkedList<>();
        List<Long> oneParkTimes = new LinkedList<>();
        executeVisitorsAsyncJoinAndForEach(AsyncService::visitAllStuffInEveryPark, visitorStuffTime -> {
            wholeTimes.add(visitorStuffTime.getWholeTime());
            tenParkTimes.addAll(visitorStuffTime.getTenParkTimes());
            oneParkTimes.addAll(visitorStuffTime.getOneParkTimes());
        });
        timeTo.setWholeVisitorStuff(wholeTimes);
        timeTo.setTenParkVisitorStuff(tenParkTimes);
        timeTo.setOneParkVisitorStuff(oneParkTimes);
    }

    private void logout() {
        log.info("logout");
        executeAdminsAsyncAndJoin(AsyncService::logout);
    }

    private void log() {
        log.info("log");
        ResultLogger resultLogger = new ResultLogger(timeTo, properties);
        resultLogger.logToConsole();
    }

    private <R> List<R> executeAdminsAsyncAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
        return admins.stream().map(asyncMethod).toList().stream().map(CompletableFuture::join)
                .collect(toList());
    }

    private <R> void executeVisitorsAsyncAndJoin(Function<AsyncService, CompletableFuture<R>> asyncMethod) {
        visitors.stream().map(asyncMethod).toList().stream().map(CompletableFuture::join).toList();
    }

    private <R> void executeVisitorsAsyncJoinAndForEach(Function<AsyncService, CompletableFuture<R>> asyncMethod,
                                                        Consumer<R> asyncResultConsumer) {
        visitors.stream().map(asyncMethod).toList().stream().map(CompletableFuture::join)
                .forEach(asyncResultConsumer);
    }

}