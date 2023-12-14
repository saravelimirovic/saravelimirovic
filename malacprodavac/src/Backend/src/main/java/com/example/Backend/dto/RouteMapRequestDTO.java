package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteMapRequestDTO {
    private String cityStart, postalCodeStart, cityEnd, postalCodeEnd;

    public RouteMapRequestDTO(String cityStart, String postalCodeStart, String cityEnd, String postalCodeEnd) {
        this.cityStart = cityStart;
        this.postalCodeStart = postalCodeStart;
        this.cityEnd = cityEnd;
        this.postalCodeEnd = postalCodeEnd;
    }
}