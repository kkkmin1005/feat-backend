package com.example.featserver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmsRepository extends JpaRepository<Alarms, Integer> {
    List<Alarms> findByToUserId(String toUserId);
}
