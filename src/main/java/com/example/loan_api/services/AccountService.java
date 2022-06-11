package com.example.loan_api.services;

import com.example.loan_api.models.auth.Account;
import com.example.loan_api.repositories.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountService implements UserDetailsService {

    private static final String NOT_FOUND = "Account not found with identifier: ";

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.findByEmail(email);
    }

    private Account findByEmail(String email) {
        return this.accountRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + email));
    }
}
