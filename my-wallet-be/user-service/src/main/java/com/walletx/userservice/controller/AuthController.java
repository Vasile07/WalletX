package com.walletx.userservice.controller;

import com.walletx.userservice.business.LoginRequest;
import com.walletx.userservice.business.LoginResponse;
import com.walletx.userservice.business.UserAuthenticationService;
import com.walletx.userservice.business.UserRegistrationRequest;
import com.walletx.userservice.business.UserRegistrationResponse;
import com.walletx.userservice.business.UserRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRegistrationService userRegistrationService;
    private final UserAuthenticationService userAuthenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(
            @Valid @RequestBody UserRegistrationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userRegistrationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(userAuthenticationService.login(request));
    }
}
