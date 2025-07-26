package com.example.library_management_be.repository;

import com.example.library_management_be.entity.BlacklistToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, Long> {
    boolean existsByToken(String token);
    void deleteByToken(String token);

}
