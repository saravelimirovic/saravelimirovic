package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveTokenFirabaseNotificationDTO {
    private String email, token;

    public SaveTokenFirabaseNotificationDTO(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
