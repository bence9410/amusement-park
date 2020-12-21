package hu.beni.amusementpark.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.ClassPathResource;

import hu.beni.amusementpark.exception.AmusementParkException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AmusementParkFilter implements Filter {
    static {
    String html = null;
    try (InputStream is = new ClassPathResource("public/index.html").getInputStream()) {
        html = new String(is.readAllBytes(), StandardCharsets.ISO_8859_1);
    } catch (IOException e) {
        throw new AmusementParkException("Could not read index.html!", e);
    }
    INDEX = html;
}
private static final String INDEX;

	private static final String[] URL_WHITELIST = { "/api/", "/img/", "/js/", "/css/", "/fonts/", "favicon" };

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String url = HttpServletRequest.class.cast(request).getRequestURL().toString();
		if (Stream.of(URL_WHITELIST).anyMatch(url::contains)) {
			chain.doFilter(request, response);
		} else {
			response.getWriter().append(INDEX).close();
		}
	}

}