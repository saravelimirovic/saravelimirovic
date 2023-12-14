package com.example.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum DeliveryMethod {
    DELIVERY("delivery"),
    IN_PERSON("inPerson");
    private final String id;


    public static DeliveryMethod getById(String id) {
        return Arrays.stream(DeliveryMethod.values())
                .filter(method -> Objects.equals(method.id, id))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("There is no delivery method with such id"));
    }

}
