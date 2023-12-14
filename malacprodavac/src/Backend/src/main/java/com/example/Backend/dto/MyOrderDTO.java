package com.example.Backend.dto;

import com.example.Backend.entity.*;
import lombok.Getter;
import lombok.Setter;

// vracam
@Getter
public class MyOrderDTO {
    private Long id;
//    private date
    private String paymentMethod;
    private String deliveryMethod;
    private String address;
    private Double quantity;
    private String measuringUnit;
    private String status;

    private Long productId;
    private String productName;
    private Double productPrice;

    private Long ownerId;
    private String ownerFullName;

    public MyOrderDTO(Order order, Product product, Double quantity) {
        this.id = order.getId();
        this.paymentMethod = order.getPaymentMethod().getId();
        this.deliveryMethod = order.getDeliveryMethod().getId();
        this.address = order.getDeliveryStreet().getName() + " " + order.getDeliveryStreetNumber() + ", " + order.getDeliveryStreet().getCity().getName();
        this.quantity = quantity;
        this.measuringUnit = product.getMeasuringUnit().getId();
        this.status = order.getStatus().getId();

        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();

        User user = product.getCompany().getUser();
        this.ownerId = order.getOwner().getId();
        this.ownerFullName = user.getFirstName() + " " + user.getLastName();
    }
}
