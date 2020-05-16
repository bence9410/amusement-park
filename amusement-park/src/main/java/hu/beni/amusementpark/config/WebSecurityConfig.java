package hu.beni.amusementpark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.config.security.ValidateingUsernamePasswordAuthenticationFilter;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final VisitorService visitorService;
	private final ObjectMapper objectMapper;
	private VisitorMapper visitorMapper;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	public void setVisitorMapper(VisitorMapper visitorMapper) {
		this.visitorMapper = visitorMapper;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(visitorService);
		authenticationProvider.setHideUserNotFoundExceptions(false);
		auth.authenticationProvider(authenticationProvider);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http //@formatter:off
            .authorizeRequests()
            	.antMatchers("/", "/webjars/**", "/index.js", "/links", "/me", "/signUp",
            			"/pages/login-and-sign-up.html", "/js/login-and-sign-up.js", "/img/**")
            	.permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .addFilterBefore(new ValidateingUsernamePasswordAuthenticationFilter(visitorService, objectMapper, visitorMapper, authenticationManager()),
            		UsernamePasswordAuthenticationFilter.class)
            .logout()
            	.logoutSuccessUrl("/")
                .and()
            .csrf()
            	.disable()
            .exceptionHandling()
            	.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")); //@formatter:on
	}

}