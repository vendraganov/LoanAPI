package com.example.loan_api.services;

import com.example.loan_api.models.dto.UserDTO;
import com.example.loan_api.models.loan.Loan;
import com.example.loan_api.models.user.User;
import com.example.loan_api.repositories.UserRepository;
import com.example.loan_api.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final String NOT_FOUND = "User not found with identifier: ";

    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public UserDTO login(String email){
        User user = this.findByEmail(email);
        List<UUID> loanIds = user.getLoans() != null ? user.getLoans().stream().map(Loan::getId).collect(Collectors.toList()) : Collections.emptyList();
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .token(jwtTokenService.generateToken(user.getAccount()))
                .build();
    }

    public User findById(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + userId));
    }

    private User findByEmail(String email) {
        return this.userRepository.findByAccountEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + email));
    }
}
