package com.example.Backend.dto;

import com.example.Backend.entity.Company;
import lombok.Getter;

import java.util.Objects;

@Getter
public class CompanyMapDTO {
    private Long id;
    private String companyName;
    private double longitude;
    private double latitude;

    public CompanyMapDTO(Company company) {
        this.id = company.getId();
        this.companyName = company.getName();
        this.longitude = company.getLongitude();
        this.latitude = company.getLatitude();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyMapDTO that = (CompanyMapDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
