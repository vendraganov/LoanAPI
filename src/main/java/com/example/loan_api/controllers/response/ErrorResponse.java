package com.example.loan_api.controllers.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.loan_api.helpers.Constants.*;

public class ErrorResponse<T> extends ResponseEntity<T> {

    private ErrorResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static ErrorResponse<?> getResponse(HttpStatus status, String message) {
        return new ErrorResponse<>(getResponseMap(status, message), status);
    }

    public static Map<String, Object> getResponseMap(HttpStatus status, String message) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put(ERROR, createErrorMap(status, message));
        responseMap.put(CODE, status.value());
        responseMap.put(STATUS, status);
        responseMap.put(MESSAGE, status.series().name());
       return responseMap;
    }

    private static Map<String, Object> createErrorMap(HttpStatus status, String message) {
        Map<String, Object> errorMapWrapper = new LinkedHashMap<>();
        Map<String, Object> errorMap = new LinkedHashMap<>();
        errorMap.put(DOMAIN, GLOBAL);
        errorMap.put(MESSAGE, message);
        errorMap.put(REASON, status.getReasonPhrase());
        errorMapWrapper.put(ERRORS, errorMap);
        return errorMapWrapper;
    }
}
