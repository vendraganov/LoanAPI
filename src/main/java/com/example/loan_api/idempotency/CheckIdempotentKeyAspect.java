package com.example.loan_api.idempotency;

import com.example.loan_api.exception.custom.IdempotentKeyExistException;
import com.example.loan_api.repository.IdempotentKeyRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Aspect
@Component
public class CheckIdempotentKeyAspect {

    private final IdempotentKeyRepository idempotentKeyRepository;

    @Around("@annotation(CheckIdempotentKey)")
    public Object validator(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        UUID idempotentKey = null;
        String uri = null;
        for (Object obj : args) {
            if (obj instanceof HttpServletRequest) {
                uri = ((HttpServletRequest) obj).getRequestURI();
                try {
                    idempotentKey = UUID.fromString(((HttpServletRequest) obj).getHeader("idempotent-key"));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid UUID signature!");
                }
            }
        }
        if (idempotentKey != null) {
            if (this.idempotentKeyRepository.existsByIdempotentKeyAndStatus(idempotentKey, IdempotentKeyStatus.USED)) {
                throw new IdempotentKeyExistException("Idempotent key exist!");
            }
            this.idempotentKeyRepository.save(IdempotentKey
                    .builder()
                    .idempotentKey(idempotentKey)
                    .domain(uri)
                    .status(IdempotentKeyStatus.IN_USE)
                    .usedOn(LocalDateTime.now())
                    .build());
            return joinPoint.proceed();
        } else {
            throw new IllegalArgumentException("Idempotent key not presented in header!");
        }
    }
}
