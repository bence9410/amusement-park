package hu.beni.amusementpark.config;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RestTemplateConfig {

	@Bean
	@Scope(SCOPE_PROTOTYPE)
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate(
				new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));
		restTemplate.getInterceptors().add(createLoggingInterceptor());
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
