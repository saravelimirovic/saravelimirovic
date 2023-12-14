package com.example.Backend.security;

import lombok.*;

@Data
public class AccountCredentials {
    private String email;
    private String password;

    @Override
    public String toString() {
        return email;
    }
}
