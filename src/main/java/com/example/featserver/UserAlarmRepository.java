package com.example.featserver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAlarmRepository extends JpaRepository<UserAlarm, Integer> {
    Optional<UserAlarm> findUserAlarmByUserId(String userId);
}
