package com.example.travel_time;

import com.example.travel_time.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
    private String email;
    private String name;
    private Role role;

}
