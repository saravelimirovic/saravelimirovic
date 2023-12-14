package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyInfoUpdateDTO {
    private String firstName, lastName, phoneNumber;

    public MyInfoUpdateDTO(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }
}
