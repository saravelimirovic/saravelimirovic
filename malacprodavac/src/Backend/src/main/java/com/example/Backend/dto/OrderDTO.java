package com.example.Backend.dto;

import jakarta.validation.constraints.Null;
import lombok.Getter;

import java.util.List;

// prihvatam
@Getter
public class OrderDTO {
    private String paymentMethod;
    private String deliveryMethod;
    private String streetName;
    private String streetNumber;
    private String cityName;
    private String status;

    private Long ownerId; // nez dal moze da posalje uopste

    private List<Double> quantities;
    private List<Long> productIds;
}
