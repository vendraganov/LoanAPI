package com.example.loan_api.controllers;


import com.example.loan_api.models.auth.AuthorizeAdminAndUser;
import com.example.loan_api.models.responses.SuccessfulResponse;
import com.example.loan_api.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/loans")
public class LoanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    @AuthorizeAdminAndUser
    @GetMapping("/types")
    public SuccessfulResponse<?> getTypes() {
        LOGGER.info("Get request: getTypes");
        return SuccessfulResponse.getResponse("loan types", this.loanService.getTypes());
    }

    @AuthorizeAdminAndUser
    @GetMapping("/schedule")
    public SuccessfulResponse<?> getSchedule() {
        LOGGER.info("Get request: getSchedule");
        return SuccessfulResponse.getResponse("loan schedule", this.loanService.getSchedule());
    }
}
