package hu.beni.amusementpark.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import hu.beni.amusementpark.ai.Aladin;

@Configuration
@ConditionalOnProperty("amusement-park.ai")
public class AIConfig {

	@Bean
	public Aladin aladin(RestTemplate restTemplate) {
		return new Aladin(restTemplate);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate(new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory()));
	}

}
