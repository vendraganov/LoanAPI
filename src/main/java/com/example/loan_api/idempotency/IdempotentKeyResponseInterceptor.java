package com.example.loan_api.idempotency;

import com.example.loan_api.repository.IdempotentKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

@RequiredArgsConstructor
@Component
public class IdempotentKeyResponseInterceptor implements HandlerInterceptor {

    private final List<Integer> statuses = Arrays.asList(200, 201, 202, 203, 204, 205, 206, 207, 208);

    private final IdempotentKeyRepository idempotentKeyRepository;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String key = request.getHeader("idempotent-key");
        if (key != null && statuses.contains(response.getStatus())) {
            UUID idempotentKeyId = UUID.fromString(key);
            Optional<IdempotentKey> idempotentKeyOptional = idempotentKeyRepository.findById(idempotentKeyId);
            if (idempotentKeyOptional.isPresent()) {
                IdempotentKey idempotentKey = idempotentKeyOptional.get();
                idempotentKey.setStatus(IdempotentKeyStatus.USED);
                idempotentKeyRepository.save(idempotentKey);
            }
        }
    }
}
