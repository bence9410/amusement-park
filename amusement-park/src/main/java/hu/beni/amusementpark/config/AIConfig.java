package hu.beni.amusementpark.config;

import hu.beni.amusementpark.ai.Aladin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConditionalOnProperty("amusement-park.ai")
public class AIConfig {

    @Bean
    public Aladin aladin(RestTemplate restTemplate) {
        return new Aladin(restTemplate);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

}
