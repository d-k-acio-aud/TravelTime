package com.example.travel_time.controller;

import com.example.travel_time.model.User;
import com.example.travel_time.service.FriendshipService;
import com.example.travel_time.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService; // нужен доступ к userId по username

    @PostMapping("/request/{receiverUsername}")
    public ResponseEntity<?> sendRequest(@AuthenticationPrincipal Jwt jwt,
                                         @PathVariable String receiverUsername) {
        String requesterUsername = jwt.getClaimAsString("sub");
        //Long userId = userService.findIdByUsername(requesterUsername);
        friendshipService.sendFriendRequestByUsername(requesterUsername, receiverUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptRequest(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long friendshipId) {
        String username = jwt.getClaimAsString("sub");
        Long userId = userService.findIdByUsername(username);
        friendshipService.acceptFriendRequest(friendshipId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<User>> getFriends(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("sub");
        Long userId = userService.findIdByUsername(username);
        List<User> friends = friendshipService.getFriends(userId);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingRequests(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getClaimAsString("sub");
        Long userId = userService.findIdByUsername(username);
        return ResponseEntity.ok(friendshipService.getPendingRequests(userId));
    }
}
