package com.example.demo.configs;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractAuthTokenFromHeader(request);
        Boolean isValidToken = jwtTokenProvider.validateToken(token);
        log.info("isValidToken :: "+isValidToken);
        if (token != null && isValidToken && !jwtTokenProvider.isTokenInvalidated(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.info("authentication ::: "+authentication.getDetails());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractAuthTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("auth-token");
        if (token != null) {
        	log.info("extractAuthTokenFromHeader :: "+token);
            return token;
        }
        return null;
    }
}

