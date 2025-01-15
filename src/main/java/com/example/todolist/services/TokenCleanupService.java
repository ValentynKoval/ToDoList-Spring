package com.example.todolist.services;

import com.example.todolist.repo.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredTokens() {
        System.out.println("Removing expired tokens");
        int deletedCount = tokenRepository.deleteAllByExpiredTrue();
        System.out.println("Removed " + deletedCount + " expired tokens");
    }
}
