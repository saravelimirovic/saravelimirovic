package com.example.Backend.dto;

import com.example.Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

// vracam
// prihvatam
@Getter
@Setter
public class UsersProfileDTO {
    private Long id;
    private String fullName;
    private String email;
    private String streetName;
    private String streetNumber;
    private String cityName;
    private String postalCode;
    private Double longitude;
    private Double latitude;
    private byte[] image; // proba
    private Integer age;
    private String phoneNumber;
    private Double avgRate;

    public UsersProfileDTO() {

    }

    @JsonIgnore
    public UsersProfileDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFirstName() + " " + user.getLastName();
        this.email = user.getEmail();
        this.streetName = user.getStreet().getName();
        this.streetNumber = user.getStreetNumber();
        this.cityName = user.getStreet().getCity().getName();
        this.postalCode = user.getPostalCode();
        this.longitude = user.getLongitude();
        this.latitude = user.getLatitude();
        this.age = user.getAge();
        this.phoneNumber = user.getPhoneNumber();
        this.avgRate = user.getAverageRating();
    }
}
