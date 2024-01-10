package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryRequestListDTO {
    private byte[] image;
    private Long id;
    private String companyName, firstNameUser, lastNameUser, cityStart, cityEnd, date, time;
}
