package com.example.travel_time.dto;

import com.example.travel_time.model.Role;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDto {
    private String password;
    private String email;
}

