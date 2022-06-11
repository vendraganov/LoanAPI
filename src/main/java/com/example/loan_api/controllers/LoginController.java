package com.example.loan_api.controllers;


import com.example.loan_api.models.responses.SuccessfulResponse;
import com.example.loan_api.models.user.UserLogin;
import com.example.loan_api.services.LoginService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static com.example.loan_api.helpers.Constants.USER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private static final String LOGIN_USER = "Login user with email: ";
    private static final String ERROR_LOGIN = "Error login user! ";
    private static final String LOGGING_REQUEST = "Logging request";

    private final LoginService loginService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public SuccessfulResponse<?> login(@Valid @RequestBody UserLogin userLogin) {
        LOGGER.info(LOGGING_REQUEST);
        try {
            final Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userLogin.getEmail(),
                            userLogin.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.info(LOGIN_USER + userLogin.getEmail());
            return SuccessfulResponse.getResponse(USER, this.loginService.login(userLogin.getEmail()));
        } catch (AuthenticationException ex) {
            LOGGER.error(ERROR_LOGIN + ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }
    }
}
