package com.example.loan_api.integration;

import com.example.loan_api.controller.LoanController;
import com.example.loan_api.idempotency.IdempotentKeyResponseInterceptor;
import com.example.loan_api.model.loan.LoanType;
import com.example.loan_api.security.JwtAuthenticationFilter;
import com.example.loan_api.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(LoanController.class)
public class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    LoanService loanService;

    @MockBean
    IdempotentKeyResponseInterceptor idempotentKeyResponseInterceptor;

    @MockBean
    JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    void method_GetTypes_ShouldReturn_Status_200_When_Called() throws Exception {
        mockMvc.perform(get("/api/v1/loans/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void method_GetTypes_ShouldReturn_LoanTypes_When_Called() throws Exception {
        LoanType loanType = new LoanType(UUID.randomUUID(), "Personal Loan", 12, BigDecimal.TEN, BigDecimal.ONE);
        List<LoanType> loanTypes = List.of(loanType);
        given(loanService.getTypes()).willReturn(loanTypes);

        mockMvc.perform(get("/api/v1/loans/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.data.loan_types", hasSize(1)))
                .andExpect(jsonPath("$[0].type", is(loanType.getType())));
    }
}
