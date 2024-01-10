package com.example.Backend.dto;

import com.example.Backend.entity.Street;
import com.example.Backend.validation.CompanyNameAvailable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

// prihvatam
@Getter
@CompanyNameAvailable
public class CompanyDTO {
    @NotBlank
    @NotNull
    private String name;
    private Integer yearOfCreation;
    private String cityName;
    private String streetName;
    private String streetNumber;
    private String postalCode;
    private String logo;
}
