package com.omarahmed42.user.utils;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import com.omarahmed42.user.exception.AuthenticationException;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtils {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static JwtAuthenticationToken getJwtAuthenticationToken() {
        return (JwtAuthenticationToken) getAuthentication();
    }

    public static String getSubject() {
        JwtAuthenticationToken token = getJwtAuthenticationToken();
        if (token == null)
            return null;
        return getJwtAuthenticationToken().getName();
    }

    public static List<GrantedAuthority> getAuthorities() {
        JwtAuthenticationToken token = getJwtAuthenticationToken();
        if (token == null)
            return Collections.emptyList();
        return (List<GrantedAuthority>) getJwtAuthenticationToken().getAuthorities();
    }

    public static boolean isAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    public static void throwIfNotAuthenticated() {
        if (!isAuthenticated())
            throw new AuthenticationException("Unauthorized: User unauthenticated");
    }
}
