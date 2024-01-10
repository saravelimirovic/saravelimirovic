package com.example.Backend.dto;

import com.example.Backend.entity.Product;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

// vracam
@Getter
public class ProductInOrderDTO {
    private Long productId;
    private String name;
    private Double quantity;
    private String measuringUnit;
    private Double price;
    private byte[] image;

    public ProductInOrderDTO(Product product, Double quantity, byte[] image) {
        this.productId = product.getId();
        this.name = product.getName();
        this.quantity = quantity;
        this.measuringUnit = product.getMeasuringUnit().getId();
        this.image = image;

        BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(product.getPrice()));
        this.price = totalPrice.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
