package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryRequestDTO {
    private String cityStart;
    private String cityEnd;
    private String date;
    private String time;
    private Long delivererId;
}
