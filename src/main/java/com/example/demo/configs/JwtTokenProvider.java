package com.example.demo.configs;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenProvider {
	
	@Autowired
	private CustomUserDetails userDetailsService;
	
	 private Set<String> invalidatedTokens = new HashSet<>();

  //  @Value("${jwt.secret}")
  //  private String secret;

  //  @Value("${jwt.expiration}")
  //  private int expiration;

	public Object generateToken(Object principal) {
	    UserDetails userDetails = (UserDetails) principal;
	    Date now = new Date();
		Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date expiryDate = calendar.getTime();
	 //   Date expiryDate = new Date(now.getTime() + 1000 * 1000);
	    log.info("expiryDate :: "+expiryDate);

	    return Jwts.builder()
	            .setSubject(userDetails.getUsername())
	            .setIssuedAt(now)
	            .setExpiration(expiryDate)
	            .signWith(SignatureAlgorithm.HS512, "example")
	            .compact();
	}

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey("example").parseClaimsJws(token).getBody().getSubject();
    }
    
    public void invalidateToken(String token) {
        // Add the token to the invalidated tokens set
        invalidatedTokens.add(token);
    }
    
    public void setTokenExpiry(String token) {
        // Extract the existing claims from the token
        Claims claims = Jwts.parser().setSigningKey("example").parseClaimsJws(token).getBody();

        // Set the new expiration time
        Date newExpiry = new Date();
        claims.setExpiration(newExpiry);

        // Re-sign the token with the updated claims
        String newToken = Jwts.builder().setClaims(claims).signWith(SignatureAlgorithm.HS256, "example").compact();

        // Perform any necessary actions with the updated token, such as storing it or updating the client
        // In this example, we assume the token is updated automatically in the client's local storage or session
    }
    
    public boolean isTokenInvalidated(String token) {
        // Check if the token exists in the invalidated tokens set
        return invalidatedTokens.contains(token);
    }

    public boolean validateToken(String token) {
    	log.info("validateToken :: "+token);
        try {
            Jwts.parser().setSigningKey("example").parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            return false;
        }
    }
    
    public Authentication getAuthentication(String token) {
    	log.info("token :: "+token);
        String username = getUsernameFromToken(token);
        log.info("username :: "+username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.info("userDetails :: "+userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
