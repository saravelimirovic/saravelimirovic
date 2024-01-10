package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductInCartDTO {
    private Long id;
    private String name, measuringUnit;
    private byte[] image;
    private Double price, quantity, quantityInStock;

    public ProductInCartDTO(Long id, byte[] image, String name, String measuringUnit, Double price, Double quantity, Double quantityInStock) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.measuringUnit = measuringUnit;
        this.price = price;
        this.quantity = quantity;
        this.quantityInStock = quantityInStock;
    }
}
