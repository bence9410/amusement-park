package hu.beni.tester.config;

import hu.beni.tester.factory.DtoFactory;
import hu.beni.tester.properties.ApplicationProperties;
import hu.beni.tester.service.AsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static hu.beni.tester.constants.Constants.*;
import static java.util.stream.Collectors.toList;

@Configuration
@RequiredArgsConstructor
public class AsyncServiceConfig {

    private final ApplicationContext ctx;
    private final ApplicationProperties properties;
    private final DtoFactory dtoFactory;

    @Bean
    public List<AsyncService> admins() {
        return createAsyncServices(properties.getNumberOf().getAdmins(), this::createAdminName);
    }

    @Bean
    public List<AsyncService> visitors() {
        return createAsyncServices(properties.getNumberOf().getVisitors(), this::createVisitorName);
    }

    private String createAdminName(int nameIndex) {
        return ADMIN + nameIndex;
    }

    private String createVisitorName(int nameIndex) {
        return VISITOR + nameIndex;
    }

    private List<AsyncService> createAsyncServices(int numberOfInstance, IntFunction<String> nameProducer) {
        return createNameStream(numberOfInstance, nameProducer).map(this::createAsyncService).collect(toList());
    }

    private Stream<String> createNameStream(int endExclusive, IntFunction<String> function) {
        return IntStream.range(0, endExclusive).mapToObj(function);
    }

    private AsyncService createAsyncService(String name) {
        return ctx.getBean(AsyncService.class, ctx.getBean(RestTemplate.class), name, dtoFactory, properties);
    }

}
