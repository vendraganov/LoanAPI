package com.example.loan_api.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.loan_api.helper.Constants.*;

public class SuccessfulResponse<T> extends ResponseEntity<T> {

    private SuccessfulResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static SuccessfulResponse<Object> getResponse(HttpStatus status) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        return getMapSuccessfulResponse(status, objectMap);
    }

    public static <T> SuccessfulResponse<Object> getResponse(HttpStatus status, String returnType, T responseObj) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(returnType, responseObj);
        return getMapSuccessfulResponse(status, objectMap);
    }

    private static SuccessfulResponse<Object> getMapSuccessfulResponse(HttpStatus status, Map<String, Object> objectMap) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put(DATA, objectMap);
        responseMap.put(CODE, status.value());
        responseMap.put(STATUS, status);
        responseMap.put(MESSAGE, status.series().name());
        return new SuccessfulResponse<>(responseMap, status);
    }
}
