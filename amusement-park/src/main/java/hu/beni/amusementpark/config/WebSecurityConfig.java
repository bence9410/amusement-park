package hu.beni.amusementpark.config;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.COULD_NOT_FIND_USER;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.ERROR;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.entity.Visitor;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.repository.VisitorRepository;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@ConditionalOnWebApplication
@EnableHypermediaSupport(type = HypermediaType.HAL)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final ObjectMapper objectMapper;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public BeniAuthenticationSuccessHandler authenticationSuccessHandler(VisitorService visitorService,
			VisitorMapper visitorMapper) {
		return new BeniAuthenticationSuccessHandler(visitorService, objectMapper, visitorMapper);
	}

	@Bean
	public UserDetailsService userDetailsService(VisitorRepository visitorRepository) {
		return new UserDetailsServiceImpl(visitorRepository);
	}

	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		authenticationProvider.setUserDetailsService(userDetailsService(null));
		authenticationProvider.setHideUserNotFoundExceptions(false);
		return authenticationProvider;
	}

	public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
		ValidateingUsernamePasswordAuthenticationFilter authenticationFilter = new ValidateingUsernamePasswordAuthenticationFilter();
		authenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler(null, null));
		authenticationFilter.setAuthenticationFailureHandler(new BeniAuthenticationFailureHandler());
		authenticationFilter.setAuthenticationManager(authenticationManagerBean());
		return authenticationFilter;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
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
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .logout()
            	.logoutSuccessUrl("/")
                .and()
            .csrf()
            	.disable()
            .exceptionHandling()
            	.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")); //@formatter:on
	}

	@RequiredArgsConstructor
	static class BeniAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

		private final VisitorService visitorService;
		private final ObjectMapper objectMapper;
		private final VisitorMapper visitorMapper;

		@Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
				Authentication authentication) throws IOException, ServletException {
			response.setHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
			response.getWriter().println(objectMapper
					.writeValueAsString(visitorMapper.toModel(visitorService.findByEmail(authentication.getName())))
					.replace("links\":[", "_links\":").replace("\"rel\":\"self\",", "\"self\":{")
					.replace("{\"rel\":\"uploadMoney\",", "\"uploadMoney\":{")
					.replace("{\"rel\":\"amusementPark\",", "\"amusementPark\":{").replace(']', '}'));
		}

	}

	@Slf4j
	static class BeniAuthenticationFailureHandler implements AuthenticationFailureHandler {

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException exception) throws IOException, ServletException {
			log.error(ERROR, exception);
			response.setStatus(HttpStatus.I_AM_A_TEAPOT.value());
			if (UsernameNotFoundException.class.isInstance(exception)
					|| BadCredentialsException.class.isInstance(exception)) {
				response.getWriter().println(exception.getMessage());
			} else {
				response.getWriter().println(UNEXPECTED_ERROR_OCCURED);
			}
		}

	}

	@RequiredArgsConstructor
	static class UserDetailsServiceImpl implements UserDetailsService {

		private final VisitorRepository visitorRepository;

		@Override
		public UserDetails loadUserByUsername(String email) {
			Visitor visitor = visitorRepository.findById(email)
					.orElseThrow(() -> new UsernameNotFoundException(String.format(COULD_NOT_FIND_USER, email)));
			return new User(email, visitor.getPassword(),
					Arrays.asList(new SimpleGrantedAuthority(visitor.getAuthority())));
		}

	}

	static class ValidateingUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

		private boolean postOnly = true;
		private int min = 5;
		private int max = 25;

		public ValidateingUsernamePasswordAuthenticationFilter() {
			setUsernameParameter("email");
		}

		@Override
		public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
			if (postOnly && !request.getMethod().equals("POST")) {
				throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
			}

			String username = validateEmail(obtainUsername(request));
			String password = validatePassword(obtainPassword(request));

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
					password);

			setDetails(request, authRequest);

			return this.getAuthenticationManager().authenticate(authRequest);
		}

		private String validateEmail(String email) {
			return Optional.ofNullable(email).filter(this::isValidEmail)
					.orElseThrow(() -> new BadCredentialsException("Email must be a well-formed email address"));
		}

		private boolean isValidEmail(String email) {
			return email.matches(".+@.+\\..+");
		}

		private String validatePassword(String credential) {
			return Optional.ofNullable(credential).map(String::trim).filter(this::isLengthBetweenMinAndMax)
					.orElseThrow(() -> new BadCredentialsException(
							String.format("Password size must be between %d and %d", min, max)));
		}

		private boolean isLengthBetweenMinAndMax(String string) {
			return string.length() >= min && string.length() <= max;
		}

	}
}