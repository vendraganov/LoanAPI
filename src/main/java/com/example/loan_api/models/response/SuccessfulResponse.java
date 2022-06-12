package com.example.loan_api.models.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.loan_api.helpers.Constants.*;

public class SuccessfulResponse<T> extends ResponseEntity<T> {

    private SuccessfulResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static SuccessfulResponse<?> getResponse() {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        return getMapSuccessfulResponse(objectMap);
    }

    public static <T> SuccessfulResponse<?> getResponse(String responseName, T responseObj) {
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(responseName, responseObj);
        return getMapSuccessfulResponse(objectMap);
    }

    private static SuccessfulResponse<?> getMapSuccessfulResponse(Map<String, Object> objectMap) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        responseMap.put(DATA, objectMap);
        responseMap.put(CODE, HttpStatus.OK.value());
        responseMap.put(STATUS, HttpStatus.OK);
        responseMap.put(MESSAGE, HttpStatus.OK.series().name());
        return new SuccessfulResponse<>(responseMap, HttpStatus.OK);
    }
}
