package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
    private Long ownerId, productId;
    private Double quantity, productTotal;

    public CartDTO(Long ownerId, Long productId, Double quantity, Double productTotal) {
        this.ownerId = ownerId;
        this.productId = productId;
        this.quantity = quantity;
        this.productTotal = productTotal;
    }
}
