package hu.beni.tester.config;

import hu.beni.tester.factory.ResourceFactory;
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
    private final ResourceFactory resourceFactory;

    @Bean
    public List<AsyncService> admins() {
        return createAsyncServices(properties.getNumberOf().getAdmins(), this::createAdminEmail);
    }

    @Bean
    public List<AsyncService> visitors() {
        return createAsyncServices(properties.getNumberOf().getVisitors(), this::createVisitorEmail);
    }

    private String createAdminEmail(int emailIndex) {
        return ADMIN + emailIndex + GMAIL;
    }

    private String createVisitorEmail(int emailIndex) {
        return VISITOR + emailIndex + GMAIL;
    }

    private List<AsyncService> createAsyncServices(int numberOfInstance, IntFunction<String> emailProducer) {
        return createEmailStream(numberOfInstance, emailProducer).map(this::createAsyncService).collect(toList());

    }

    private Stream<String> createEmailStream(int endExclusive, IntFunction<String> function) {
        return IntStream.range(0, endExclusive).mapToObj(function);
    }

    private AsyncService createAsyncService(String email) {
        return ctx.getBean(AsyncService.class, ctx.getBean(RestTemplate.class), email, resourceFactory, properties);
    }

}
