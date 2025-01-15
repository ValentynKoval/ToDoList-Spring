package com.example.todolist.repo;

import com.example.todolist.models.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.expired = true")
    int deleteAllByExpiredTrue();
}
