package com.example.Backend.dto;

import com.example.Backend.entity.Delivery;
import com.example.Backend.entity.Route;
import com.example.Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

// vracam
@Getter
public class DeliveryResDTO {
    private Long id;
    private String delivererName;
    private String route;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private java.util.Date dateOfDeparture; // datum polaska

    public DeliveryResDTO(Delivery delivery) {
        this.id = delivery.getId();

        User user = delivery.getDeliverer();
        this.delivererName = user.getFirstName() + " " + user.getLastName();

        Route route1 = delivery.getRoute();
        this.route = route1.getStartPointCity().getName() + " - " + route1.getEndPointCity().getName();

        this.dateOfDeparture = delivery.getDeliveryDate();
    }
}
