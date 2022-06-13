package com.example.loan_api.services;

import com.example.loan_api.models.dto.UserDTO;
import com.example.loan_api.models.user.User;
import com.example.loan_api.models.user.UserLogin;
import com.example.loan_api.models.user.UserLoginHistory;
import com.example.loan_api.repositories.UserLoginHistoryRepository;
import com.example.loan_api.repositories.UserRepository;
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
        this.authenticateUser(userLogin);
        User user = this.findByEmail(userLogin.getEmail());
        this.saveUserLoginHistory(user);
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
        this.userLoginHistoryRepository.save(userLoginHistory);
    }
}
