package com.example.travel_time.controller;

import com.example.travel_time.dto.FriendDto;
import com.example.travel_time.dto.FriendRequestDto;
import com.example.travel_time.model.User;
import com.example.travel_time.service.FriendshipService;
import com.example.travel_time.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;
    private final UserService userService;

    @PostMapping("/request/{receiverUsername}")
    public ResponseEntity<?> sendRequest(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable String receiverUsername) {
        User requester = userService.findByUsername(userDetails.getUsername());
        friendshipService.sendFriendRequestByUsername(requester.getUsername(), receiverUsername);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptRequest(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long friendshipId) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        friendshipService.acceptFriendRequest(friendshipId, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FriendDto>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<FriendDto> friends = friendshipService.getFriends(currentUser.getId());
        return ResponseEntity.ok(friends);
    }

//    @GetMapping("/pending")
//    public ResponseEntity<List<FriendRequestDto>> getPendingRequests(@AuthenticationPrincipal UserDetails userDetails) {
//        User currentUser = userService.findByUsername(userDetails.getUsername());
//        return ResponseEntity.ok(friendshipService.getAllPendingRequests(currentUser.getId()));
//    }

    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestDto>> getIncomingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(friendshipService.getIncomingRequests(currentUser.getId()));
    }

    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendRequestDto>> getOutgoingRequests(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        return ResponseEntity.ok(friendshipService.getOutgoingRequests(currentUser.getId()));
    }

    @DeleteMapping("/cancel/{friendshipId}")
    public ResponseEntity<?> cancelOrDelete(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long friendshipId) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        friendshipService.deleteFriendship(friendshipId, currentUser.getId());
        return ResponseEntity.ok().build();
    }


}
