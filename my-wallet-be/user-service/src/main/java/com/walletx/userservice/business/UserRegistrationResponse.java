package com.walletx.userservice.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserRegistrationResponse {

    private final UUID id;
    private final String name;
    private final String email;
}
