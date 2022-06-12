package com.example.loan_api.idempotency;

import com.example.loan_api.exceptions.custom.IdempotentKeyExistException;
import com.example.loan_api.repositories.IdempotentKeyRepository;
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

    private static final String IDEMPOTENT_KEY_HEADER = "idempotent-key";
    private static final String IDEMPOTENT_KEY_EXIST = "Idempotent key exist!";
    private static final String INVALID_UUID_SIGNATURE = "Invalid UUID signature!";
    private static final String IDEMPOTENT_KEY_NOT_PRESENTED = "Idempotent key not presented in header!";

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
                    idempotentKey = UUID.fromString(((HttpServletRequest) obj).getHeader(IDEMPOTENT_KEY_HEADER));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(INVALID_UUID_SIGNATURE);
                }
            }
        }
        if (idempotentKey != null) {
            if (this.idempotentKeyRepository.existsByIdempotentKey(idempotentKey)) {
                throw new IdempotentKeyExistException(IDEMPOTENT_KEY_EXIST);
            }
            this.idempotentKeyRepository.save(IdempotentKey
                    .builder()
                    .idempotentKey(idempotentKey)
                    .domain(uri)
                    .usedOn(LocalDateTime.now())
                    .build());
            return joinPoint.proceed();
        } else {
            throw new IllegalArgumentException(IDEMPOTENT_KEY_NOT_PRESENTED);
        }
    }
}
