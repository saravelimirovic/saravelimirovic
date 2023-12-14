package com.example.Backend.dto;

import com.example.Backend.entity.Company;
import com.example.Backend.entity.User;
import lombok.Getter;

// vracam
@Getter
public class NumInfoDTO {
    private Integer numOfProducts;
    private Integer numOfSales;
    private Integer numOfFollowers;

    private String email;
    private String companyName;
    private byte[] logo;

    public NumInfoDTO(Company company, Integer numOfProducts, Integer numOfSales, Integer numOfFollowers, byte[] img) {
        this.numOfProducts = numOfProducts;
        this.numOfSales = numOfSales;
        this.numOfFollowers = numOfFollowers;

        User user = company.getUser();
        this.email = user.getEmail();

        this.companyName = company.getName();
        this.logo = img;
    }
}
