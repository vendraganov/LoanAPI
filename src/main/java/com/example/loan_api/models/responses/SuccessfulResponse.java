package com.example.loan_api.models.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.loan_api.helpers.Constants.*;

public class SuccessfulResponse<T> extends ResponseEntity<T> {

    private SuccessfulResponse(T body, HttpStatus status) {
        super(body, status);
    }

    public static <T> SuccessfulResponse<?> getResponse(String responseName, T responseObj) {
        Map<String, Object> responseMap = new LinkedHashMap<>();
        Map<String, Object> objectMap = new LinkedHashMap<>();
        objectMap.put(responseName, responseObj);
        responseMap.put(DATA, objectMap);
        responseMap.put(CODE, HttpStatus.OK.value());
        responseMap.put(STATUS, HttpStatus.OK);
        responseMap.put(MESSAGE, HttpStatus.OK.series().name());
        return new SuccessfulResponse<>(responseMap, HttpStatus.OK);
    }
}
