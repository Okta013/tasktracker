package ru.anikeeva.petprojcets.tasktracker.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.anikeeva.petprojcets.tasktracker.models.Position;
import ru.anikeeva.petprojcets.tasktracker.models.User;
import ru.anikeeva.petprojcets.tasktracker.models.enums.EnumRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("""
            FROM User u WHERE (:startBirthday IS NULL OR u.birthday >= :startBirthday) \
            AND (:endBirthday IS NULL OR u.birthday <= :endBirthday) \
            AND (:isEnabled IS NULL OR u.isEnabled = :isEnabled) \
            AND (:isDeleted IS NULL OR u.isDeleted = :isDeleted) \
            AND (:role IS NULL OR u.role = :role) \
            AND (:position IS NULL OR u.position = :position)""")
    Page<User> findAllUsersWithFilters(@Param("startBirthday") LocalDate startBirthday,
                                       @Param("endBirthday") LocalDate endBirthday,
                                       @Param("isEnabled") Boolean isEnabled,
                                       @Param("isDeleted") Boolean isDeleted,
                                       @Param("role") EnumRole role,
                                       @Param("position") Position position,
                                       Pageable pageable);

    @Query("""
            FROM User u WHERE (:username IS NULL OR u.username LIKE :username) \
            AND (:phone IS NULL OR u.phone LIKE :phone) \
            AND (:email IS NULL OR u.email LIKE :email) \
            AND (:birthday IS NULL OR u.birthday = :birthday)""")
    List<User> findAllUsersWithParameters(@Param("username") String username, @Param("phone") String phone,
                                          @Param("email") String email, @Param("birthday") LocalDate birthDay);
}