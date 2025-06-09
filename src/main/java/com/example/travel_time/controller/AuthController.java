package com.example.travel_time.controller;


import com.example.travel_time.dto.AuthRequestDto;
import com.example.travel_time.dto.TokenResponseDto;
import com.example.travel_time.dto.RegisterRequestDto;
import com.example.travel_time.service.AuthService;
import com.example.travel_time.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;
    private final JwtService jwtService;




    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody AuthRequestDto authRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.authenticate(authRequest, response));
    }


    @PostMapping("/register")
    public ResponseEntity<TokenResponseDto> register(@RequestBody RegisterRequestDto authRequest, HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(authRequest, response));
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();


    }
}

