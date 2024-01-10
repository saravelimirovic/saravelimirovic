package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

// prihvatam
@Getter
public class ProductDTO {
    private String name;
    private String description;
    private String measuringUnit;
    private String productCategory;
    private Double quantity;
    private Double price;
    private String image;
}
