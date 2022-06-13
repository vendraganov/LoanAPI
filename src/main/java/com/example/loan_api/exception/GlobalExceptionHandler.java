package com.example.loan_api.exception;

import com.example.loan_api.exception.custom.IdempotentKeyExistException;
import com.example.loan_api.exception.custom.LoanPaymentException;
import com.example.loan_api.controller.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String ERROR = "error";
    private static final String WARNING = "warning";

    @ExceptionHandler(IdempotentKeyExistException.class)
    public final ErrorResponse<?> handleIdempotentKeyExistException(IdempotentKeyExistException e) {
        logError(e, WARNING);
        return ErrorResponse.getResponse(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public final ErrorResponse<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(LoanPaymentException.class)
    public final ErrorResponse<?> handleLoanPaymentException(LoanPaymentException e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ErrorResponse<?> handleIllegalArgumentException(IllegalArgumentException e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public final ErrorResponse<?> handleAuthenticationException(AuthenticationException e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.UNAUTHORIZED, "Your email or password was incorrect!");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ErrorResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public final ErrorResponse<?> handleAllExceptions(Exception e) {
        logError(e, ERROR);
        return ErrorResponse.getResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private void logError(Exception e, String type) {
        String message = e.getCause() == null ? e.getMessage() : e.getMessage() + System.lineSeparator() + e.getLocalizedMessage();
        if (type.equals(ERROR)) {
            LOGGER.error(message);
        }
        LOGGER.warn(message);
    }
}