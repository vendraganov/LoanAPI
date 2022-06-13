package com.example.loan_api.models.auth.anotation;

import com.example.loan_api.models.auth.Role;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize(Role.ADMIN_AND_USER_AUTHORITY)
public @interface AuthorizeAdminAndUser {
}
