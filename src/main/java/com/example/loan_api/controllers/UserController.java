package com.example.loan_api.controllers;

import com.example.loan_api.models.auth.anotation.AuthorizeAll;
import com.example.loan_api.controllers.response.SuccessfulResponse;
import com.example.loan_api.models.user.UserLogin;
import com.example.loan_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private static final String POST_REQUEST_LOGIN = "Post request: login";

    private final UserService userService;

    @AuthorizeAll
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> login(@Valid @RequestBody UserLogin userLogin) {
        LOGGER.info(POST_REQUEST_LOGIN);
        return SuccessfulResponse.getResponse(HttpStatus.OK, USER, this.userService.login(userLogin));
    }
}
