package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvailableDeliveryDTO {
    private byte[] userImage;
    private Long userId;
    private String firstNameUser, lastNameUser, from, to, date, time;
}
