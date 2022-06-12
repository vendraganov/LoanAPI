package com.example.loan_api.controllers;


import com.example.loan_api.models.anotation.AuthorizeAdmin;
import com.example.loan_api.models.anotation.AuthorizeAdminAndUser;
import com.example.loan_api.models.anotation.AuthorizeUser;
import com.example.loan_api.idempotency.CheckIdempotentKey;
import com.example.loan_api.models.dto.PostLoanDTO;
import com.example.loan_api.models.dto.PostPaymentDTO;
import com.example.loan_api.models.dto.PostWaivePaymentDTO;
import com.example.loan_api.models.response.SuccessfulResponse;
import com.example.loan_api.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;

import static com.example.loan_api.helpers.Constants.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    private static final String GET_REQUEST_GET_LOANS = "Get request: getLoans";
    private static final String GET_REQUEST_GET_TYPES = "Get request: getTypes";
    private static final String GET_REQUEST_GET_SCHEDULE = "Get request: getSchedule";
    private static final String POST_REQUEST_APPLY = "Post request: apply";
    private static final String POST_REQUEST_PAYMENT = "Post request: payment";
    private static final String POST_REQUEST_WAIVE_PAYMENT = "Post request: waive-payment";

    private final LoanService loanService;

    @AuthorizeAdminAndUser
    @GetMapping(path = "/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> getLoans(@PathVariable UUID userId) {
        LOGGER.info(GET_REQUEST_GET_LOANS);
        return SuccessfulResponse.getResponse(LOANS, this.loanService.getLoans(userId));
    }

    @AuthorizeUser
    @GetMapping(path = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> getTypes() {
        LOGGER.info(GET_REQUEST_GET_TYPES);
        return SuccessfulResponse.getResponse(LOAN_TYPES, this.loanService.getTypes());
    }

    @AuthorizeAdminAndUser
    @GetMapping(path = "/schedule/{loanId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> getSchedule(@PathVariable UUID loanId, @PathVariable UUID userId) {
        LOGGER.info(GET_REQUEST_GET_SCHEDULE);
        return SuccessfulResponse.getResponse(LOAN_SCHEDULE, this.loanService.getSchedule(loanId, userId));
    }

    @CheckIdempotentKey
    @AuthorizeUser
    @PostMapping(path = "/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> apply(HttpServletRequest request, @Valid @RequestBody PostLoanDTO postLoanDTO) {
        LOGGER.info(POST_REQUEST_APPLY);
        this.loanService.apply(postLoanDTO);
        return SuccessfulResponse.getResponse();
    }

    @CheckIdempotentKey
    @AuthorizeAdminAndUser
    @PostMapping(path = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> payment(HttpServletRequest request, @Valid @RequestBody PostPaymentDTO postPaymentDTO) {
        LOGGER.info(POST_REQUEST_PAYMENT);
        this.loanService.payment(postPaymentDTO);
        return SuccessfulResponse.getResponse();
    }

    @CheckIdempotentKey
    @AuthorizeAdmin
    @PostMapping(path = "/waive-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> waivePayment(HttpServletRequest request, @Valid @RequestBody PostWaivePaymentDTO postWaivePaymentDTO) {
        LOGGER.info(POST_REQUEST_WAIVE_PAYMENT);
        this.loanService.waivePayment(postWaivePaymentDTO);
        return SuccessfulResponse.getResponse();
    }
}
