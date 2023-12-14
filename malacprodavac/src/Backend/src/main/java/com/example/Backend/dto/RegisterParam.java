package com.example.Backend.dto;

import lombok.Getter;

// prihvatam
@Getter
public class RegisterParam {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String city;
    private String street;
    private String streetNumber;
    private String phoneNumber;
    private String postalCode;
    private Integer role;
    private byte[] image;
}
