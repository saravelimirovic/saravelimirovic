package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyInfoUpdateDTO {
    private String firstName, lastName, phoneNumber;
    private byte[] image;

    public MyInfoUpdateDTO(String firstName, String lastName, String phoneNumber, byte[] image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }
}
