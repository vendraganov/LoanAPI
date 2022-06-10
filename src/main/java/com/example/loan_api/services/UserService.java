package com.example.loan_api.services;

import com.example.loan_api.controllers.dtos.UserDTO;
import com.example.loan_api.models.User;
import com.example.loan_api.repositories.UserRepository;
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

    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserDTO login(String email){
        User user = this.findByEmail(email);
        return UserDTO.builder()
                .token(jwtTokenUtil.generateToken(user))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.findByEmail(email);
    }

    private User findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException(String.format(NOT_FOUND, email)));
    }
}
