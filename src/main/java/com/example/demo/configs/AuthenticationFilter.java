package com.example.demo.configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
//    private final AuthService authService;

    
    public AuthenticationFilter() {
        
    }
    
//    public AuthenticationFilter(AuthService authService) {
//        this.authService = authService;
//    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.isNotEmpty(authorization)) {
//            AuthResponse authResponse = authService.doAuthenticate();

        	User u = new User("john.doe");
            Role r1 = new Role("TELE_SALES_AGENT");
            Role r2 = new Role("FIELD_SALES_AGENT");
            AuthResponse authResponse = new AuthResponse(u, Arrays.asList(r1, r2));
            log.info("authResponse: {}", authResponse);

            // Set new authentication token
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authResponse.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authResponse.getUser().getUsername(), null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
    
    @Data @AllArgsConstructor
    class AuthResponse {
    	
    	private User user;
    	private List<Role> roles;
    }
    
    @Data @AllArgsConstructor
    class User {
    	private String username;
    }
    
    @Data @AllArgsConstructor
    class Role {
    	private String name;
    }
}
