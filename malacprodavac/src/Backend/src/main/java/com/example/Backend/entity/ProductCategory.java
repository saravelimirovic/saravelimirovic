package com.example.Backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ProductCategory {
    FRESH_FRUITS("freshFruits"),
    DRIED_FRUITS("driedFruits"),
    VEGETABLES("vegetables"),
    MEAT_PRODUCTS("meatProducts"),
    DAIRY_PRODUCTS("dairyProducts"),
    ORGANIC_PRODUCTS("organicProducts"),
    SPICES("spices"),
    HERBS("herbs"), // lekovito bilje
    FLOWERS_AND_SEEDLINGS("flowersAndSeedlings"),
    BEE_PRODUCTS("beeProducts"),
    CANNED_FOODS("cannedFoods"), // konzerve, i tipa ono ajvar
    HANDMADE_CRAFTS("handMadeCrafts");
    private final String id;

    public String getId() {
        return id;
    }

    public static ProductCategory getById(String id) {
        return Arrays.stream(ProductCategory.values())
                .filter(method -> Objects.equals(method.id, id))
                .findFirst()
                .orElseThrow( () -> new IllegalArgumentException("There is no product category with such id"));
    }
}
