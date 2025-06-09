package com.example.travel_time.repository;

import com.example.travel_time.model.Friendship;
import com.example.travel_time.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT f FROM Friendship f WHERE (f.requester = :user OR f.receiver = :user) AND f.accepted = true")
    List<Friendship> findAcceptedFriendships(@Param("user") User user);


    Optional<Friendship> findByRequesterAndReceiver(User requester, User receiver);

    List<Friendship> findByReceiverAndAcceptedFalse(User receiver);

    List<Friendship> findByRequesterAndAcceptedFalse(User requester); // исправлено

    Optional<Friendship> findByAcceptedFalseAndRequesterAndReceiver(User requester, User receiver); // исправлено


}
