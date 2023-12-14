package com.example.Backend.dto;

import com.example.Backend.entity.MeasuringUnit;
import com.example.Backend.entity.Product;
import com.example.Backend.entity.ProductCategory;
import com.example.Backend.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// vracam
@Getter
@Setter
public class ProductDetailsDTO {
    private Long productId;
    private String productName;
    private Double productPrice;
    private String productDescription;
    private String productCategory;
    private Double quantity;
    private String measuringUnit;
    private Double totalRating;
    private Integer ratingCount;
    private Long ownerId;
    private String ownerFullName;
    private boolean isFavourite;
    private byte[] image;


    public ProductDetailsDTO(Product product, boolean isFavourite) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productDescription = product.getDescription();
        this.productPrice = product.getPrice();
        this.totalRating = product.getAverageRating();
        this.ratingCount = product.getTotalCountRating();

        User user = product.getCompany()
                .getUser();

        this.ownerId = user.getId();
        this.ownerFullName = user.getFirstName() + " " + user.getLastName();
        this.quantity = product.getQuantity();
        this.measuringUnit = product.getMeasuringUnit().getId();
        this.productCategory = product.getProductCategory().getId();
        this.isFavourite = isFavourite;
    }
}
