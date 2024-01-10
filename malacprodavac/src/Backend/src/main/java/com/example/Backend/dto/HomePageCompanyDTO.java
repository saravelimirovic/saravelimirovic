package com.example.Backend.dto;

import com.example.Backend.entity.Company;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import lombok.Getter;
import java.util.Objects;

@Getter
public class HomePageCompanyDTO {
    private Long id;
    private String companyName;
    private byte[] logo;
    private Double totalRate;
    private boolean isFollowed;

    public HomePageCompanyDTO(Company company, Double totalRate, boolean isFollowed, byte[] logo) {
        this.id = company.getId();
        this.companyName = company.getName();
        this.logo = logo;
        this.totalRate = totalRate;
        this.isFollowed = isFollowed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HomePageCompanyDTO that = (HomePageCompanyDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
