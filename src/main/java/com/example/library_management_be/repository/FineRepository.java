package com.example.library_management_be.repository;

import com.example.library_management_be.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, Long> {
    // Additional query methods can be defined here if needed
}
