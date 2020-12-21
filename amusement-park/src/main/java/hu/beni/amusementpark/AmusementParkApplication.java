package hu.beni.amusementpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import hu.beni.amusementpark.filter.AmusementParkFilter;

@SpringBootApplication
public class AmusementParkApplication {

	public static void main(String[] args) {
		SpringApplication.run(AmusementParkApplication.class, args);
	}

	@Bean
	public FilterRegistrationBean<AmusementParkFilter> loggingFilter() {
		FilterRegistrationBean<AmusementParkFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new AmusementParkFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}

}