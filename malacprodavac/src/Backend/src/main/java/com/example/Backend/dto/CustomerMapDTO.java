package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerMapDTO {
    private String firstName;
    private String lastName;
    private double longitude;
    private double latitude;

    public CustomerMapDTO(String firstName, String lastName, double longitude, double latitude) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
