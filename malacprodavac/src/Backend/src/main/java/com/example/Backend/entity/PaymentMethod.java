package com.example.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("card"),
    ON_DELIVERY("onDelivery");
    private final String id;

    public static PaymentMethod getById(String id) {
        return Arrays.stream(PaymentMethod.values())
                .filter(method -> Objects.equals(method.id, id))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("There is no payment method with such id"));
    }
}
