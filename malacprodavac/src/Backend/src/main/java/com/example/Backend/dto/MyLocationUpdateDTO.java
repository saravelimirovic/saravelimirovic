package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyLocationUpdateDTO {
    private String city, postalCode, street, streetNumber;
    private double longitude, latitude;

    public MyLocationUpdateDTO(String city, String postalCode, String street, String streetNumber, double longitude, double latitude) {
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.streetNumber = streetNumber;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
