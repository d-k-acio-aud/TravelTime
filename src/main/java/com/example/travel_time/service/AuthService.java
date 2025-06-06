package com.example.travel_time.service;

import com.example.travel_time.AuthRequest;
import com.example.travel_time.model.Role;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.UserRepository;
import com.example.travel_time.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ResponseEntity<String> login(AuthRequest authRequest, HttpServletResponse response) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        if (!passwordEncoder.matches(authRequest.getPassword(), userDetails.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        var jwtToken = JwtUtil.generateToken(userDetails.getUsername());
        jwtUtil.addJwtToCookie(jwtToken, response);
        response.setHeader("Location", "/home");
        return null;
    }

    public ResponseEntity<String> register(AuthRequest authRequest) {
        if (userRepository.findByUsername(authRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(authRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        newUser.setEmail(authRequest.getEmail());
        //newUser.setName(authRequest.getName());
        //newUser.setRole(authRequest.getRole());//Динамически устанавливаем роль
        newUser.setRole(Role.USER); // Явно устанавливаем роль
        userRepository.save(newUser);

        return ResponseEntity.ok("Registered");
    }

//    public void logout(HttpServletResponse response) throws IOException {
//        jwtUtil.logout(response);
//        response.sendRedirect("/login");
//    }
}
