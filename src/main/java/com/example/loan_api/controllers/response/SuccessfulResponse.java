package com.example.loan_api.controllers.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.loan_api.helpers.Constants.*;

public class SuccessfulResponse<T> extends ResponseEntity<T> {

    private SuccessfulResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static SuccessfulResponse<?> getResponse(HttpStatus status) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        return getMapSuccessfulResponse(status, objectMap);
    }

    public static <T> SuccessfulResponse<?> getResponse(HttpStatus status, String responseName, T responseObj) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(responseName, responseObj);
        return getMapSuccessfulResponse(status, objectMap);
    }

    private static SuccessfulResponse<?> getMapSuccessfulResponse(HttpStatus status, Map<String, Object> objectMap) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put(DATA, objectMap);
        responseMap.put(CODE, status.value());
        responseMap.put(STATUS, status);
        responseMap.put(MESSAGE, status.series().name());
        return new SuccessfulResponse<>(responseMap, status);
    }
}
