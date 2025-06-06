package com.example.travel_time.dto;

import com.example.travel_time.model.User;

public record UserSearchDto(Long id, String username, boolean friend, boolean pending) {
    public UserSearchDto(User user, boolean friend, boolean pending) {
        this(user.getId(), user.getUsername(), friend, pending);
    }
}
