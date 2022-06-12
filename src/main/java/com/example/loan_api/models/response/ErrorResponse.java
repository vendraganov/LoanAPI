package com.example.loan_api.models.response;

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
        Error error = Error.builder()
                .domain(GLOBAL)
                .message(message)
                .reason(status.getReasonPhrase())
                .build();
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(ERRORS, error);
        responseMap.put(ERROR, objectMap);
        responseMap.put(CODE, status.value());
        responseMap.put(STATUS, status);
        responseMap.put(MESSAGE, status.series().name());
       return responseMap;
    }
}
