package com.example.Backend.repository;

import com.example.Backend.entity.Verification;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;

public interface VerificationRepository extends ListCrudRepository<Verification, Long> {
    Optional<Verification> findVerificationByEmailAndCode(String email, String code);
    Optional<Verification> findByEmail(String email);
}
