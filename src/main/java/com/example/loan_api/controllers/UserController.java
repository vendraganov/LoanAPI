package com.example.loan_api.controllers;

import com.example.loan_api.models.anotation.AuthorizeAll;
import com.example.loan_api.models.response.SuccessfulResponse;
import com.example.loan_api.models.user.UserLogin;
import com.example.loan_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.example.loan_api.helpers.Constants.USER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String LOGIN_USER = "Login user with email: ";
    private static final String LOGGING_REQUEST = "Logging request";

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @AuthorizeAll
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> login(@Valid @RequestBody UserLogin userLogin) throws AuthenticationException {
        LOGGER.info(LOGGING_REQUEST);
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLogin.getEmail(),
                        userLogin.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        LOGGER.info(LOGIN_USER + userLogin.getEmail());
        return SuccessfulResponse.getResponse(USER, this.userService.login(userLogin.getEmail()));
    }
}
