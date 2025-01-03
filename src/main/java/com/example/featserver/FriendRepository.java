package com.example.featserver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
    List<Friend> findByToUserId(String toUserId);
    List<Friend> findByToUserIdAndIsFriendFalse(String toUserId);
    List<Friend> findByFromUserIdAndIsFriendTrue(String fromUserId);
}
