package com.example.loan_api.repositories;

import com.example.loan_api.idempotency.IdempotentKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IdempotentKeyRepository extends JpaRepository<IdempotentKey, UUID> {
    boolean existsByIdempotentKey(UUID idempotentKey);
}
