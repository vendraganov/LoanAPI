package com.example.loan_api.services;

import com.example.loan_api.models.dtos.UserDTO;
import com.example.loan_api.models.user.User;
import com.example.loan_api.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public UserDTO login(String email){
        User user = this.userService.findByEmail(email);
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .token(jwtTokenService.generateToken(user.getAccount()))
                .build();
    }
}
