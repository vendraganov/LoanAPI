package com.example.loan_api.models.auth;

public enum Role {
    ADMIN, USER;

    public static final String USER_AUTHORITY = "hasAuthority('USER')";
    public static final String ADMIN_AUTHORITY = "hasAuthority('ADMIN')";
    public static final String ADMIN_AND_USER_AUTHORITY = "hasAuthority('ADMIN') or hasAuthority('USER')";
}
