package hu.beni.tester.config;

import hu.beni.tester.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.concurrent.Executor;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class ExecutorAndRestTemplateConfig {

    private final ApplicationProperties applicationProperties;

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(applicationProperties.getNumberOf().getVisitors());
        executor.initialize();
        return executor;
    }

    @Bean
    @Scope(SCOPE_PROTOTYPE)
    public RestTemplate restTemplate() {
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        restTemplate.setUriTemplateHandler(defaultUriBuilderFactory);
        return restTemplate;
    }

}