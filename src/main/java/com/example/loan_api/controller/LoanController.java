package com.example.loan_api.controller;


import com.example.loan_api.model.auth.anotation.AuthorizeAdmin;
import com.example.loan_api.model.auth.anotation.AuthorizeAdminAndUser;
import com.example.loan_api.model.auth.anotation.AuthorizeUser;
import com.example.loan_api.idempotency.CheckIdempotentKey;
import com.example.loan_api.model.dto.PostLoanDTO;
import com.example.loan_api.model.dto.PostPaymentDTO;
import com.example.loan_api.controller.response.SuccessfulResponse;
import com.example.loan_api.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

import static com.example.loan_api.helper.Constants.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class);

    private final LoanService loanService;

    @AuthorizeUser
    @GetMapping(path = "/user/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> getAllByCurrentUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        LOGGER.info("Get request:  getAllByCurrentUser");
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOANS, this.loanService.getAllByCurrentUser(principal.getName()));
    }

    @AuthorizeAdmin
    @GetMapping(path = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> getAllByUserId(@PathVariable UUID userId) {
        LOGGER.info("Get request: getAllByUserId");
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOANS, this.loanService.getAllByUserId(userId));
    }

    @AuthorizeUser
    @GetMapping(path = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> getTypes() {
        LOGGER.info("Get request: getTypes");
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOAN_TYPES, this.loanService.getTypes());
    }

    @AuthorizeUser
    @GetMapping(path = "/schedule/{loanId}/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> getSchedule(HttpServletRequest request, @PathVariable UUID loanId) {
        Principal principal = request.getUserPrincipal();
        LOGGER.info("Get request: getSchedule");
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOAN_SCHEDULE, this.loanService.getSchedule(loanId, principal.getName()));
    }

    @AuthorizeAdmin
    @GetMapping(path = "/schedule/{loanId}/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> getUserSchedule(@PathVariable UUID loanId, @PathVariable UUID userId) {
        LOGGER.info("Get request: getSchedule");
        return SuccessfulResponse.getResponse(HttpStatus.OK, LOAN_SCHEDULE, this.loanService.getUserSchedule(loanId, userId));
    }

    @CheckIdempotentKey
    @AuthorizeUser
    @PostMapping(path = "/application", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> apply(HttpServletRequest request, @Valid @RequestBody PostLoanDTO postLoanDTO) {
        LOGGER.info("Post request: apply");
        return SuccessfulResponse.getResponse(HttpStatus.CREATED, LOAN_ID, this.loanService.apply(postLoanDTO));
    }

    @CheckIdempotentKey
    @AuthorizeAdminAndUser
    @PostMapping(path = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> payment(HttpServletRequest request, @Valid @RequestBody PostPaymentDTO postPaymentDTO) {
        LOGGER.info("Post request: payment");
        this.loanService.payment(postPaymentDTO, false);
        return SuccessfulResponse.getResponse(HttpStatus.ACCEPTED);
    }

    @CheckIdempotentKey
    @AuthorizeAdmin
    @PostMapping(path = "/payment/waiver", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulResponse<Object> waivePayment(HttpServletRequest request, @Valid @RequestBody PostPaymentDTO postPaymentDTO) {
        LOGGER.info("Post request: waivePayment");
        this.loanService.payment(postPaymentDTO, true);
        return SuccessfulResponse.getResponse(HttpStatus.ACCEPTED);
    }
}
