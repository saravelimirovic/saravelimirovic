package com.example.Backend.dto;

import com.example.Backend.entity.City;
import com.example.Backend.entity.Route;
import com.example.Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;


// prihvatam
@Getter
public class DeliveryDTO {
    private String startPointCityName;
    private String startPointCityPostalCode;
    private String endPointCityName;
    private String endPointCityPostalCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private java.util.Date dateOfDeparture;
}
