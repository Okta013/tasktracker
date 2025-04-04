package ru.anikeeva.petprojcets.tasktracker.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.anikeeva.petprojcets.tasktracker.repositories.SupportiveTokenRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenCleanupService {
    private final SupportiveTokenRepository supportiveTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        supportiveTokenRepository.deleteByExpiryDateBefore(new Date());
    }
}