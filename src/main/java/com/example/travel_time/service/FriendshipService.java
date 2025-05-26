package com.example.travel_time.service;

import com.example.travel_time.model.Friendship;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.FriendshipRepository;
import com.example.travel_time.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public void sendFriendRequestByUsername(String requesterUsername, String receiverUsername) {
        if (requesterUsername.equals(receiverUsername)) {
            throw new IllegalArgumentException("Нельзя добавить себя в друзья");
        }

        User requester = userRepository.findByUsername(requesterUsername)
                .orElseThrow(() -> new EntityNotFoundException("Requester not found"));

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new EntityNotFoundException("Receiver not found"));

        if (friendshipRepository.findByRequesterAndReceiver(requester, receiver).isPresent()) {
            throw new IllegalStateException("Запрос уже существует");
        }

        friendshipRepository.save(new Friendship(null, requester, receiver, false));
    }

    public void acceptFriendRequest(Long friendshipId, Long userId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friend request not found"));

        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new SecurityException("Нельзя подтвердить чужую заявку");
        }

        friendship.setAccepted(true);
        friendshipRepository.save(friendship);
    }

    public List<User> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Friendship> friendships = friendshipRepository.findByRequesterOrReceiverAndAcceptedTrue(user, user);

        return friendships.stream()
                .map(f -> f.getRequester().equals(user) ? f.getReceiver() : f.getRequester())
                .toList();
    }

    public List<Friendship> getPendingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return friendshipRepository.findByReceiverAndAcceptedFalse(user);
    }

    public boolean areFriends(User a, User b) {
        return friendshipRepository.findByRequesterOrReceiverAndAcceptedTrue(a, a)
                .stream()
                .anyMatch(f -> f.getRequester().equals(b) || f.getReceiver().equals(b));
    }
}
