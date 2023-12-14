package com.example.Backend.dto;

import com.example.Backend.entity.Order;
import com.example.Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

// vracam
@Getter
public class OrdersListDTO {
    private Long orderId;
    private String fullNameCustomer;
    private String deliveryCity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private java.util.Date dateOrderIsMade;
    private Double totalPrice;

    public OrdersListDTO(Order order, Double totalPrice) {
        this.orderId = order.getId();
        this.fullNameCustomer = order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName();
        if(order.getDeliveryStreet() != null && order.getDeliveryStreet().getCity() != null)
            this.deliveryCity = order.getDeliveryStreet().getCity().getName();
        this.dateOrderIsMade = order.getDateOrderIsMade();
        this.totalPrice = totalPrice;
    }
}
