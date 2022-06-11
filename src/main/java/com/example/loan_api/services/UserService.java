package com.example.loan_api.services;

import com.example.loan_api.models.dtos.UserDTO;
import com.example.loan_api.models.auth.Account;
import com.example.loan_api.repositories.AccountRepository;
import com.example.loan_api.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private static final String NOT_FOUND = "User with email %s not found!";

    private final AccountRepository accountRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserDTO login(String email){
        Account account = this.findByEmail(email);
        return UserDTO.builder()
                .token(jwtTokenUtil.generateToken(account))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.findByEmail(email);
    }

    private Account findByEmail(String email) {
        return this.accountRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format(NOT_FOUND, email)));
    }
}
