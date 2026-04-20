package hu.bence.amusementpark.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
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
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/", "/index.html", "/favicon.ico", "/assets/*", "/api/links",
                                    "/api/me", "/api/login", "/api/signUp")
                            .permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .logout(logout -> {
                    logout.logoutUrl("/api/logout")
                            .logoutSuccessUrl("/");
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}