package hu.beni.amusementpark.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@ConditionalOnWebApplication
@EnableMethodSecurity
@EnableJdbcHttpSession
public class WebSecurityConfig {

    @Bean
    SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/", "/api/links", "/api/me", "/api/signUp", "/img/**","/css/**","/js/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .logout(logout -> {
                    logout.logoutUrl("/api/logout")
                            .logoutSuccessUrl("/");
                })
                .exceptionHandling(exception -> {
                    exception.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"));
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}