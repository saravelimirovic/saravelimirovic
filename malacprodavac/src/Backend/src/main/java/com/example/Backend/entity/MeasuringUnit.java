package com.example.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MeasuringUnit {
    KILOGRAM("kilogram"),
    LITAR("litar");
    private final String id;

    public static MeasuringUnit getById(String id) {
        return Arrays.stream(MeasuringUnit.values())
                .filter(method -> Objects.equals(method.id, id))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("There is no measuring method with such id"));
    }
}
