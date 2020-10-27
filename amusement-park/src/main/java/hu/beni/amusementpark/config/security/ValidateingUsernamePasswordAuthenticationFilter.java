package hu.beni.amusementpark.config.security;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.beni.amusementpark.constants.Constants;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;

public class ValidateingUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private final String usernameParameter = "email";
	private final String passwordParameter = "password";

	public ValidateingUsernamePasswordAuthenticationFilter(VisitorService visitorService, ObjectMapper objectMapper,
			VisitorMapper visitorMapper, AuthenticationManager authenticationManager) {
		super(new AntPathRequestMatcher("/api/login", "POST"));
		setAuthenticationSuccessHandler(
				new AmusementParkAuthenticationSuccessHandler(visitorService, objectMapper, visitorMapper));
		setAuthenticationFailureHandler(new AmusementParkAuthenticationFailureHandler());
		setAuthenticationManager(authenticationManager);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		String username = validateEmail(obtainUsername(request));
		String password = validatePassword(obtainPassword(request));

		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

		return this.getAuthenticationManager().authenticate(authRequest);
	}

	private String validateEmail(String email) {
		return Optional.ofNullable(email).filter(this::isValidEmail).orElseThrow(
				() -> new BadCredentialsException("Email must be well-formed, for example: somebody@example.com"));
	}

	private boolean isValidEmail(String email) {
		return email.matches(Constants.EMAIL_REGEXP);
	}

	private String validatePassword(String credential) {
		return Optional.ofNullable(credential).map(String::trim).filter(this::isPasswordValid)
				.orElseThrow(() -> new BadCredentialsException(
						"Password must contain at least one upper and lowercase characters and number and the length must be between 8-25."));
	}

	private boolean isPasswordValid(String password) {
		return password.matches(Constants.PASSWORD_REGEXP);
	}

	@Nullable
	protected String obtainPassword(HttpServletRequest request) {
		return request.getParameter(passwordParameter);
	}

	@Nullable
	protected String obtainUsername(HttpServletRequest request) {
		return request.getParameter(usernameParameter);
	}

}
