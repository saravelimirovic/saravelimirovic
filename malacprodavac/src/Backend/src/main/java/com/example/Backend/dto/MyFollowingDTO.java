package com.example.Backend.dto;

import com.example.Backend.entity.Following;
import lombok.Getter;

// vracam
@Getter
public class MyFollowingDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    public MyFollowingDTO(Following following) { // novo
        this.id = following.getId();
        this.firstName = following.getUserFollowing().getFirstName();
        this.lastName = following.getUserFollowing().getLastName();
        this.email = following.getUserFollowing().getEmail();
    }
}
