package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CoordinatesDTO {
    private double longitude;
    private double latitude;

    public CoordinatesDTO(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
