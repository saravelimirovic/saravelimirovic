package com.example.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum StatusOrder {
//    ORDERED("ordered"),
    READY_FOR_DELIVERY("readyForDelivery"),
    OUT_FOR_DELIVERY("outForDelivery"),
//    ON_THE_WAY("onTheWay"),
    DELIVERED("delivered"),
    CANCELLED("cancelled"),
    FAILED("failed");
    private final String id;


    public static StatusOrder getById(String id) {
        return Arrays.stream(StatusOrder.values())
                .filter(method -> Objects.equals(method.id, id))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("There is order status with such id"));
    }
}
