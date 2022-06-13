package com.example.loan_api.controllers;


import com.example.loan_api.models.auth.anotation.AuthorizeAdmin;
import com.example.loan_api.models.auth.anotation.AuthorizeAdminAndUser;
import com.example.loan_api.models.auth.anotation.AuthorizeUser;
import com.example.loan_api.idempotency.CheckIdempotentKey;
import com.example.loan_api.models.dto.PostLoanDTO;
import com.example.loan_api.models.dto.PostPaymentDTO;
import com.example.loan_api.models.dto.PostWaivePaymentDTO;
import com.example.loan_api.controllers.response.SuccessfulResponse;
import com.example.loan_api.services.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public SuccessfulResponse<?> getAllByUserId(@PathVariable UUID userId) {
        LOGGER.info(GET_REQUEST_GET_LOANS);
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOANS, this.loanService.getAllByUserId(userId));
    }

    @AuthorizeUser
    @GetMapping(path = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> getTypes() {
        LOGGER.info(GET_REQUEST_GET_TYPES);
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOAN_TYPES, this.loanService.getTypes());
    }

    @AuthorizeAdminAndUser
    @GetMapping(path = "/schedule/{loanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> getSchedule(@PathVariable UUID loanId) {
        LOGGER.info(GET_REQUEST_GET_SCHEDULE);
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOAN_SCHEDULE, this.loanService.getSchedule(loanId));
    }

    @CheckIdempotentKey
    @AuthorizeUser
    @PostMapping(path = "/apply", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> apply(HttpServletRequest request, @Valid @RequestBody PostLoanDTO postLoanDTO) {
        LOGGER.info(POST_REQUEST_APPLY);
        this.loanService.apply(postLoanDTO);
        return SuccessfulResponse.getResponse(HttpStatus.CREATED);
    }

    @CheckIdempotentKey
    @AuthorizeAdminAndUser
    @PostMapping(path = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> payment(HttpServletRequest request, @Valid @RequestBody PostPaymentDTO postPaymentDTO) {
        LOGGER.info(POST_REQUEST_PAYMENT);
        this.loanService.payment(postPaymentDTO, false);
        return SuccessfulResponse.getResponse(HttpStatus.ACCEPTED);
    }

    @CheckIdempotentKey
    @AuthorizeAdmin
    @PostMapping(path = "/waive-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<?> waivePayment(HttpServletRequest request, @Valid @RequestBody PostWaivePaymentDTO postWaivePaymentDTO) {
        LOGGER.info(POST_REQUEST_WAIVE_PAYMENT);
        this.loanService.payment(postWaivePaymentDTO, true);
        return SuccessfulResponse.getResponse(HttpStatus.ACCEPTED);
    }
}
