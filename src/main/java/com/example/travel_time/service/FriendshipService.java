package com.example.travel_time.service;

import com.example.travel_time.dto.FriendDto;
import com.example.travel_time.model.Friendship;
import com.example.travel_time.model.User;
import com.example.travel_time.repository.FriendshipRepository;
import com.example.travel_time.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.travel_time.dto.FriendRequestDto;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    public record PendingRequestDto(Long id, String username) {}

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

    public List<FriendDto> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Friendship> friendships = friendshipRepository.findAcceptedFriendships(user);

        return friendships.stream()
                .map(f -> {
                    User friend = f.getRequester().equals(user) ? f.getReceiver() : f.getRequester();
                    return new FriendDto(f.getId(), friend.getUsername());
                })
                .toList();
    }


    //    public List<Friendship> getPendingRequests(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        return friendshipRepository.findByReceiverAndAcceptedFalse(user);
//    }
        public List<PendingRequestDto> getPendingRequests(Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            return friendshipRepository.findByReceiverAndAcceptedFalse(user).stream()
                    .map(f -> new PendingRequestDto(f.getId(), f.getRequester().getUsername()))
                    .toList();
        }


//    public boolean areFriends(User a, User b) {
//        return friendshipRepository.findByRequesterOrReceiverAndAcceptedTrue(a, a)
//                .stream()
//                .anyMatch(f -> f.getRequester().equals(b) || f.getReceiver().equals(b));
//    }
//    public List<FriendRequestDto> getPendingRequestDtos(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found"));
//
//        return friendshipRepository.findByReceiverAndAcceptedFalse(user).stream()
//                .map(f -> new FriendRequestDto(
//                        f.getId(),
//                        f.getRequester().getId(),
//                        f.getRequester().getUsername()
//                ))
//                .toList();
//    }
    public boolean isFriend(User currentUser, User other) {
        return friendshipRepository.findAcceptedFriendships(currentUser)
                .stream()
                .anyMatch(f -> f.getRequester().equals(other) || f.getReceiver().equals(other));
    }

    public boolean hasPendingRequest(User currentUser, User other) {
        return friendshipRepository.findByAcceptedFalseAndRequesterAndReceiver(currentUser, other).isPresent()
                || friendshipRepository.findByAcceptedFalseAndRequesterAndReceiver(other, currentUser).isPresent();
    }

    public List<FriendRequestDto> getAllPendingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return Stream.concat(
                friendshipRepository.findByReceiverAndAcceptedFalse(user).stream()
                        .map(f -> new FriendRequestDto(f.getId(), f.getRequester().getUsername(), true)),
                friendshipRepository.findByRequesterAndAcceptedFalse(user).stream()
                        .map(f -> new FriendRequestDto(f.getId(), f.getReceiver().getUsername(), false))
        ).collect(Collectors.toList());
    }
    public int getFriendsCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return getFriends(user.getId()).size();
    }

    public List<FriendRequestDto> getIncomingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return friendshipRepository.findByReceiverAndAcceptedFalse(user).stream()
                .map(f -> new FriendRequestDto(f.getId(), f.getRequester().getUsername(), true))
                .toList();
    }

    public List<FriendRequestDto> getOutgoingRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return friendshipRepository.findByRequesterAndAcceptedFalse(user).stream()
                .map(f -> new FriendRequestDto(f.getId(), f.getReceiver().getUsername(), false))
                .toList();
    }


    public void deleteFriendship(Long friendshipId, Long currentUserId) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new EntityNotFoundException("Friendship not found"));

        if (!friendship.getRequester().getId().equals(currentUserId) &&
                !friendship.getReceiver().getId().equals(currentUserId)) {
            throw new SecurityException("You cannot delete this friendship");
        }

        friendshipRepository.delete(friendship);
    }



}
