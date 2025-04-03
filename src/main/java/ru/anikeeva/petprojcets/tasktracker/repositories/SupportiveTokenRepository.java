package ru.anikeeva.petprojcets.tasktracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anikeeva.petprojcets.tasktracker.models.SupportiveToken;
import ru.anikeeva.petprojcets.tasktracker.models.User;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupportiveTokenRepository extends JpaRepository<SupportiveToken, UUID> {
    Optional<SupportiveToken> findByToken(String token);

    @Query("SELECT s.user FROM SupportiveToken s WHERE s.token = :token")
    User findUserByToken(String token);

    @Modifying
    @Query("DELETE FROM SupportiveToken t WHERE t.expiryDate < :date")
    void deleteByExpiryDateBefore(@Param("date") Date date);
}