package com.omarahmed42.catalog.utils;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class SecurityUtils {

    public static Authentication getAuthentication() {
        log.info("Getting authentication");
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static JwtAuthenticationToken getJwtAuthenticationToken() {
        log.info("Getting JWT");
        return (JwtAuthenticationToken) getAuthentication();
    }

    public static String getSubject() {
        log.info("Getting subject");
        JwtAuthenticationToken token = getJwtAuthenticationToken();
        if (token == null)
            return null;
        return getJwtAuthenticationToken().getName();
    }

    public static List<GrantedAuthority> getAuthorities() {
        log.info("Getting authorities");
        JwtAuthenticationToken token = getJwtAuthenticationToken();
        if (token == null)
            return Collections.emptyList();
        return (List<GrantedAuthority>) getJwtAuthenticationToken().getAuthorities();
    }

}
