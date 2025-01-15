package com.example.todolist.services;

import com.example.todolist.models.Token;
import com.example.todolist.models.User;
import com.example.todolist.repo.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration}")
    private Duration accessTokenDuration;

    @Value("${security.jwt.refresh-token.expiration}")
    private Duration refreshTokenDuration;

    @Autowired
    private TokenRepository tokenRepository;

    public Map<String, String> getTokens(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().toString());

        String accessToken = generateToken(claims, user.getEmail(), accessTokenDuration);
        String refreshToken = generateToken(claims, user.getEmail(), refreshTokenDuration);
        Token token = new Token();
        token.setToken(refreshToken);
        token.setUser(user);
        tokenRepository.save(token);

        Map<String, String> response = new HashMap<>();
        response.put("access_token", accessToken);
        response.put("refresh_token", refreshToken);
        return response;
    }

    private String generateToken(Map<String, Object> claims, String email, Duration duration) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + duration.toMillis()))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String refreshToken (String refreshToken) throws Exception {
        Optional<Token> optionalToken = tokenRepository.findByToken(refreshToken);
        if (optionalToken.isEmpty()) {
            throw new Exception("Refresh token not found");
        }
        if (isTokenExpired(refreshToken)) {
            Token token = optionalToken.get();
            token.setExpired(true);
            tokenRepository.save(token);
            throw new Exception("Refresh token expired");
        }
        Map<String, Object> claims = extractAllClaims(refreshToken);
        String email = extractUsername(refreshToken);
        return generateToken(claims, email, accessTokenDuration);
    }

    @Transactional
    public void revokeToken(String token) {
        tokenRepository.deleteByToken(token);
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
