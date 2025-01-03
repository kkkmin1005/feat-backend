package com.example.featserver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Integer> {
    List<Posts> findByUserId(String userId);
    Optional<Posts> findByUserIdAndDate(String userId, LocalDate date);
}
