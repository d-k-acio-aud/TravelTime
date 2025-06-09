package com.example.travel_time.service;


import com.example.travel_time.model.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class JwtService {
    private static final Dotenv dotenv = Dotenv.configure().filename("amazon.env").load();
    private static final String SECRET = dotenv.get("SECRET");
    private static final SecretKeySpec KEY = new SecretKeySpec(SECRET.getBytes(), SignatureAlgorithm.HS256.getJcaName());




    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(User user) {
        return Jwts.builder()
                .claim("role", user.getRole().name())
                .claim("username", user.getUsername())  // добавляем username
                .setSubject(user.getEmail())            // subject — это email
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        if (userDetails instanceof com.example.travel_time.model.User user) {
            return email.equals(user.getEmail()) && !isTokenExpired(token);
        }


        return false;
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}

