package com.example.travel_time.security;


import com.example.travel_time.repository.UserRepository;
import com.example.travel_time.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("JwtFilter called");
        final String jwt = extractJwtFromRequest(request);


        if (jwt != null) {
            try {
                String email = jwtService.extractEmail(jwt);


                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    var user = userRepository.findByEmail(email)
                            .orElse(null);


                    if (user != null && jwtService.isTokenValid(jwt, user)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());




                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );


                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                Cookie expiredCookie = new Cookie("jwt", null);
                expiredCookie.setHttpOnly(true);
                expiredCookie.setMaxAge(0); // Удалить куку
                expiredCookie.setPath("/"); // Важно: указывать путь
                response.addCookie(expiredCookie);




            } catch (Exception e) {
            }
        }


        filterChain.doFilter(request, response);
    }


    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }


        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }


        return null;
    }
}

