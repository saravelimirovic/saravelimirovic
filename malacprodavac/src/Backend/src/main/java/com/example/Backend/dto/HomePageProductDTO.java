package com.example.Backend.dto;

import com.example.Backend.entity.Product;
import lombok.Getter;
import lombok.Setter;

// vracam
@Getter
@Setter
public class HomePageProductDTO {
    private Long id;
    private String name;
    private Double price;
    private Double rate;
    private boolean isFavourite;
    private String description;
    private byte[] image;

    public HomePageProductDTO(Product product, boolean isFavourite, byte[] img) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.rate = product.getAverageRating();
        this.isFavourite = isFavourite;
        this.description = product.getDescription();
        this.image = img;
    }
}
