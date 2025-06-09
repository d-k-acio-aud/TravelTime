package com.example.travel_time.service;
import com.example.travel_time.dto.AuthRequestDto;
import com.example.travel_time.dto.TokenResponseDto;
import com.example.travel_time.dto.RegisterRequestDto;
import com.example.travel_time.model.Role;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    public void addJwtToCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60 * 30);
        response.addCookie(cookie);
    }
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302
        response.setHeader("Location", "/login");
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }


    public TokenResponseDto authenticate(AuthRequestDto request, HttpServletResponse response) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        addJwtToCookie(jwtToken, response);
        return new TokenResponseDto(jwtToken);
    }
    public TokenResponseDto register(RegisterRequestDto request, HttpServletResponse response) {
        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();


        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        addJwtToCookie(jwtToken, response);
        return new TokenResponseDto(jwtToken);
    }


}

