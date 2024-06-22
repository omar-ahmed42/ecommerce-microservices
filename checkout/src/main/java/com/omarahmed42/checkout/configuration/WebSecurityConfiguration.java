package com.omarahmed42.checkout.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.omarahmed42.checkout.converter.KeycloakJwtRolesConverter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Value("${keycloak.client-id}")
    private String kcClientId;

    @Value("${keycloak.issuer-url}")
    private String tokenIssuerUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        DelegatingJwtGrantedAuthoritiesConverter authoritiesConverter = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new KeycloakJwtRolesConverter(kcClientId));

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .anyRequest().authenticated())
                .oauth2ResourceServer(server -> server
                        .jwt()
                        .jwtAuthenticationConverter(
                                jwt -> new JwtAuthenticationToken(jwt, authoritiesConverter.convert(jwt))));
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(tokenIssuerUrl);
    }

    @Bean
    GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
