package com.example.loan_api.service;

import com.example.loan_api.model.dto.UserDTO;
import com.example.loan_api.model.user.User;
import com.example.loan_api.model.user.UserLogin;
import com.example.loan_api.model.user.UserLoginHistory;
import com.example.loan_api.repository.UserLoginHistoryRepository;
import com.example.loan_api.repository.UserRepository;
import com.example.loan_api.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private static final String NOT_FOUND = "User not found with identifier: ";

    private final UserRepository userRepository;
    private final UserLoginHistoryRepository userLoginHistoryRepository;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;

    public UserDTO login(UserLogin userLogin)  {
        authenticateUser(userLogin);
        User user = findByEmail(userLogin.getEmail());
        saveUserLoginHistory(user);
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .token(jwtTokenService.generateToken(user.getAccount()))
                .build();
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + userId));
    }

    public User findByEmail(String email) {
        return userRepository.findByAccountEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(NOT_FOUND + email));
    }

    private void authenticateUser(UserLogin userLogin) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLogin.getEmail(),
                        userLogin.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void saveUserLoginHistory(User user) {
        UserLoginHistory userLoginHistory = UserLoginHistory.builder()
                .loginOn(LocalDateTime.now())
                .user(user)
                .build();
        userLoginHistoryRepository.save(userLoginHistory);
    }
}
