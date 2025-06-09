package com.example.travel_time.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
}


