package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteMapResponseDTO {
    private double lonStart, latStart, lonEnd, latEnd;

    public RouteMapResponseDTO(double lonStart, double latStart, double lonEnd, double latEnd) {
        this.lonStart = lonStart;
        this.latStart = latStart;
        this.lonEnd = lonEnd;
        this.latEnd = latEnd;
    }
}
