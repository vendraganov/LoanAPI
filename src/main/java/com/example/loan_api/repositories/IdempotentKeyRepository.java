package com.example.loan_api.repositories;

import com.example.loan_api.idempotency.IdempotentKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IdempotentKeyRepository extends JpaRepository<IdempotentKey, UUID> {
}
