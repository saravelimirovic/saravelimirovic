package com.example.Backend.dto;

import com.example.Backend.entity.Product;
import lombok.Getter;

// vracam
@Getter
public class ProductInOrderDTO {
    private Long productId;
    private String name;
    private Double quantity;
    private String measuringUnit;
    private Double price;
//    private String image;

    public ProductInOrderDTO(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.measuringUnit = product.getMeasuringUnit().getId();
        this.price = product.getPrice();
    }
}
