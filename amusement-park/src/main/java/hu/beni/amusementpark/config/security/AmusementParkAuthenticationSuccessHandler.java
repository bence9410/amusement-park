package hu.beni.amusementpark.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.beni.amusementpark.mapper.VisitorMapper;
import hu.beni.amusementpark.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AmusementParkAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final VisitorService visitorService;
    private final ObjectMapper objectMapper;
    private final VisitorMapper visitorMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/hal+json");
        visitorService.getOffMachineAndLeavePark(authentication.getName());
        response.getWriter()
                .println(objectMapper
                        .writeValueAsString(
                                visitorMapper.toModelWithPhoto(visitorService.findByEmail(authentication.getName())))
                        .replace("links\":[", "_links\":").replace("\"rel\":\"self\",", "\"self\":{")
                        .replace("{\"rel\":\"uploadMoney\",", "\"uploadMoney\":{")
                        .replace("{\"rel\":\"amusementPark\",", "\"amusementPark\":{").replace(']', '}'));
    }
}
