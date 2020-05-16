package hu.beni.amusementpark.config.security;

import static hu.beni.amusementpark.constants.ErrorMessageConstants.ERROR;
import static hu.beni.amusementpark.constants.ErrorMessageConstants.UNEXPECTED_ERROR_OCCURED;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmusementParkAuthenticationFailureHandler implements AuthenticationFailureHandler {

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
