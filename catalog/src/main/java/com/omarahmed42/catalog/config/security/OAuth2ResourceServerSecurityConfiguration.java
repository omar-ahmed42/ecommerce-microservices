package com.omarahmed42.catalog.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/**/categories/**")).permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/api/**/products/**")).permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(configurer -> configurer.jwt(Customizer.withDefaults()));
        return http.build();
    }

    private org.springframework.security.web.util.matcher.AntPathRequestMatcher antMatcher(HttpMethod method,
            String pattern) {
        return org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher(method, pattern);
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

}