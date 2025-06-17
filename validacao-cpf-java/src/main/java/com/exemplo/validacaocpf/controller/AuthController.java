package com.exemplo.validacaocpf.controller;

import com.exemplo.validacaocpf.model.LoginRequest;
import com.exemplo.validacaocpf.model.User;
import com.exemplo.validacaocpf.security.JwtTokenProvider;
import com.exemplo.validacaocpf.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            String token = jwtTokenProvider.generateToken(username, user.getRoles());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", username,
                    "roles", user.getRoles()));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas!");
        }
    }
}
