package com.example.loan_api.idempotency;

import com.example.loan_api.exception.custom.IdempotentKeyExistException;
import com.example.loan_api.repository.IdempotentKeyRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Aspect
@Component
public class CheckIdempotentKeyAspect {

    private final IdempotentKeyRepository idempotentKeyRepository;

    @Around("@annotation(CheckIdempotentKey)")
    public Object validator(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        UUID idempotentKeyId = null;
        String uri = null;
        String methodName = joinPoint.getSignature().getName();

        for (Object obj : args) {
            if (obj instanceof HttpServletRequest) {
                uri = ((HttpServletRequest) obj).getRequestURI();
                try {
                    idempotentKeyId = UUID.fromString(((HttpServletRequest) obj).getHeader("idempotent-key"));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid UUID signature!");
                }
            }
        }

        if (idempotentKeyId != null) {
            Optional<IdempotentKey> idempotentKeyOptional = idempotentKeyRepository.findById(idempotentKeyId);
            if (idempotentKeyOptional.isPresent()) {
                IdempotentKey idempotentKey = idempotentKeyOptional.get();

                if (idempotentKey.getStatus().equals(IdempotentKeyStatus.USED)) {
                    if (idempotentKey.getDomain().equals(uri)) {
                        throw new IdempotentKeyExistException("Request completed!", idempotentKey.getMethodName(), HttpStatus.OK);
                    }
                    throw new IdempotentKeyExistException("Idempotent key exist!", HttpStatus.CONFLICT);
                } else if (idempotentKey.getStatus().equals(IdempotentKeyStatus.IN_USE)) {
                    if (!idempotentKey.getDomain().equals(uri)) {
                        updateIdempotentKey(idempotentKey, uri, methodName);
                    }
                    return joinPoint.proceed();
                }
            }
            saveIdempotentKey(idempotentKeyId, uri, methodName);
            return joinPoint.proceed();
        } else {
            throw new IllegalArgumentException("Idempotent key not presented in header!");
        }
    }

    private void saveIdempotentKey(UUID idempotentKeyIn, String uri, String methodName) {
        idempotentKeyRepository.save(IdempotentKey
                .builder()
                .idempotentKey(idempotentKeyIn)
                .domain(uri)
                .methodName(methodName)
                .status(IdempotentKeyStatus.IN_USE)
                .usedOn(LocalDateTime.now())
                .build());
    }

    private void updateIdempotentKey(IdempotentKey idempotentKey, String uri, String methodName) {
        idempotentKey.setDomain(uri);
        idempotentKey.setMethodName(methodName);
        idempotentKey.setStatus(IdempotentKeyStatus.IN_USE);
        idempotentKey.setUsedOn(LocalDateTime.now());
        idempotentKeyRepository.save(idempotentKey);
    }
}
