package com.eex.application.controller;

import com.eex.application.dto.AuthResponse;
import com.eex.application.dto.LoginRequest;
import com.eex.domain.entity.User;
import com.eex.domain.service.UserService;
import com.eex.domain.util.JwtUtil;
import com.eex.domain.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: " + loginRequest.getUsername());

        List<User> users = userService.getUsersByUsername(loginRequest.getUsername());

        if (users.isEmpty()) {
            logger.warn("User not found: " + loginRequest.getUsername());
            return new ResponseEntity<>(new AuthResponse(null, "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }

        User user = users.get(0);

        if (!PasswordUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Invalid password for user: " + loginRequest.getUsername());
            return new ResponseEntity<>(new AuthResponse(null, "Invalid credentials"), HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getClientId());
        logger.info("Login successful for user: " + loginRequest.getUsername());

        return new ResponseEntity<>(new AuthResponse(token, "Authentication successful"), HttpStatus.OK);
    }

    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("Validating token");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Missing or invalid Authorization header");
            AuthResponse response = new AuthResponse(null, "Missing or invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        Integer userId = jwtUtil.validateAndGetUserId(token);

        if (userId == null) {
            logger.warn("Invalid or expired token");
            AuthResponse response = new AuthResponse(null, "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        logger.info("Valid token for user: " + userId);
        AuthResponse response = new AuthResponse(token, "Valid token for user: " + userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}