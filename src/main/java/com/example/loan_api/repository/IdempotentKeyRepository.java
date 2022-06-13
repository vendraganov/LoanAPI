package com.example.loan_api.repository;

import com.example.loan_api.idempotency.IdempotentKey;
import com.example.loan_api.idempotency.IdempotentKeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdempotentKeyRepository extends JpaRepository<IdempotentKey, UUID> {
    boolean existsByIdempotentKeyAndStatus(UUID idempotentKey, IdempotentKeyStatus status);
}
