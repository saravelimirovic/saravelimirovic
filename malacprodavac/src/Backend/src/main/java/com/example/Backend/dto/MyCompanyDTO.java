package com.example.Backend.dto;

import com.example.Backend.entity.Company;
import lombok.Getter;
import lombok.Setter;

// vracam
@Getter
@Setter
public class MyCompanyDTO {
    private Long id;
    private String name;
    private Integer yearOfCreation;
    private String address;
//    private String logo;


//    public MyCompanyDTO(Long id, String name, Integer yearOfCreation, String streetName, String streetNumber, String cityName) {
//        this.id = id;
//        this.name = name;
//        this.yearOfCreation = yearOfCreation;
//        this.address = streetName + " " + streetNumber + ", " + cityName;
//    }

    public MyCompanyDTO(Company company) {
        this.id = company.getId();
        this.name = company.getName();
        this.yearOfCreation = company.getYearOfCreation();
        this.address = company.getStreet().getName() + " " + company.getStreetNumber() + ", " + company.getStreet().getCity().getName();
    }
}
