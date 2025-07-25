package com.example.library_management_be.repository;

import com.example.library_management_be.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); // Find user by email

    boolean existsByEmail(String email); // Check if user exists by email

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user WHERE is_verified = false AND created_at < :cutoffTime", nativeQuery = true)
    void deleteUnverifiedUsersBefore(@Param("cutoffTime") Instant cutoffTime);
}
