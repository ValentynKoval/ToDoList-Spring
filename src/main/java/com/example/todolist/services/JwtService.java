package com.example.todolist.services;

import com.example.todolist.models.Token;
import com.example.todolist.repo.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.access-token-duration}")
    private Duration accessTokenDuration;

    @Value("${security.jwt.refresh-token-duration}")
    private Duration refreshTokenDuration;

    private TokenRepository tokenRepository;
    private UserService userService;

    public Map<String, String> generateTokens(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = List.of(userDetails.getAuthorities().iterator().next().getAuthority());
        claims.put("roles", roles);
        String accessToken = generateToken(claims, userDetails, accessTokenDuration);
        String refreshToken = generateToken(claims, userDetails, refreshTokenDuration);
        Token token = new Token();
        token.setToken(refreshToken);
        token.setUser(userService.getUserByEmail(userDetails.getUsername()));
        tokenRepository.save(token);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        return response;
    }

    private String generateToken(Map<String, Object> claims, UserDetails userDetails, Duration duration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(java.util.Date.from(java.time.Instant.now()))
                .setExpiration(java.util.Date.from(java.time.Instant.now().plus(duration)))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
