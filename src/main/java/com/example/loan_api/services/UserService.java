package com.example.loan_api.services;

import com.example.loan_api.models.user.User;
import com.example.loan_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final String NOT_FOUND = "User not found with identifier: ";

    private final UserRepository userRepository;

    public User findByEmail(String email) {
        return this.userRepository.findByAccountEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + email));
    }

    public User findById(UUID userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + userId));
    }
}
