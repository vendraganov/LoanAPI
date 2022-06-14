package com.example.loan_api.controller;

import com.example.loan_api.model.auth.anotation.AuthorizeAll;
import com.example.loan_api.controller.response.SuccessfulResponse;
import com.example.loan_api.model.user.UserLogin;
import com.example.loan_api.service.UserService;
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

import static com.example.loan_api.helper.Constants.USER;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private static final String POST_REQUEST_LOGIN = "Post request: login";

    private final UserService userService;

    @AuthorizeAll
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> login(@Valid @RequestBody UserLogin userLogin) {
        LOGGER.info(POST_REQUEST_LOGIN);
        return SuccessfulResponse.getResponse(HttpStatus.OK, USER, this.userService.login(userLogin));
    }
}
