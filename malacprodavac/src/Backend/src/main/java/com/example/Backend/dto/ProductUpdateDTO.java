package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

// prihvatam
@Getter
@Setter
public class ProductUpdateDTO {
    private Long productId;
    private String productName;
    private Double productPrice;
    private String productDescription;
    private String productCategory;
    private Double quantity;
    private String measuringUnit;
    // trebalo bi i slika
}
