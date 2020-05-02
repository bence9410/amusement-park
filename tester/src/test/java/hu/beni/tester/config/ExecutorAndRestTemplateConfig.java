package hu.beni.tester.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class ExecutorAndRestTemplateConfig {

	private final ObjectMapper objectMapper;

	@PostConstruct
	public void init() {
		objectMapper.registerModule(new JavaTimeModule());
	}

	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(100);
		executor.initialize();
		return executor;
	}

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		// restTemplate.getInterceptors().add(createLoggingInterceptor());
		return restTemplate;
	}

	private ClientHttpRequestInterceptor createLoggingInterceptor() {
		return (HttpRequest request, byte[] body, ClientHttpRequestExecution execution) -> {
			log.info("Request: { URL: {}, Method: {}, Body: {} }", request.getURI(), request.getMethod(),
					new String(body));
			ClientHttpResponse response = execution.execute(request, body);
			log.info("Response: { Status: {}, Body: {} }", response.getStatusCode().toString(),
					new String(FileCopyUtils.copyToByteArray(response.getBody())));
			return response;
		};
	}
}