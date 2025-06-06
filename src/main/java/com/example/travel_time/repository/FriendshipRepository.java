package com.example.travel_time.repository;

import com.example.travel_time.model.Friendship;
import com.example.travel_time.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByRequesterOrReceiverAndAcceptedTrue(User requester, User receiver);

    Optional<Friendship> findByRequesterAndReceiver(User requester, User receiver);

    List<Friendship> findByReceiverAndAcceptedFalse(User receiver);

    List<Friendship> findByRequesterAndAcceptedFalse(User requester); // исправлено

    Optional<Friendship> findByAcceptedFalseAndRequesterAndReceiver(User requester, User receiver); // исправлено
}
