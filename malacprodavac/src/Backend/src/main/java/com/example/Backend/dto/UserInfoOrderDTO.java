package com.example.Backend.dto;

import com.example.Backend.entity.DeliveryMethod;
import com.example.Backend.entity.Order;
import com.example.Backend.entity.User;
import lombok.Getter;

// vracam
@Getter
public class UserInfoOrderDTO {
    private Long customerId;
    private String fullName;
    private String cityName;
    private String streetName;
    private String phoneNumber;
    private boolean delivery;
    private String paymentMethod;

    public UserInfoOrderDTO(Order order) {
        User user = order.getCustomer();

        this.customerId = user.getId();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.cityName = user.getStreet().getCity().getName() + ", " + user.getPostalCode();
        this.streetName = user.getStreet().getName() + " " + user.getStreetNumber();
        this.phoneNumber = user.getPhoneNumber();

        if(order.getDeliveryMethod() == DeliveryMethod.DELIVERY) {
            this.delivery = true;
        }

        this.paymentMethod = order.getPaymentMethod().getId();
    }
}
