package com.walletx.userservice.business;

public interface UserAuthenticationService {
    LoginResponse login(LoginRequest request);
}
