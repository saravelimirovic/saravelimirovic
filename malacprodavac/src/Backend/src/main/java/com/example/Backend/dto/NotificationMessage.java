package com.example.Backend.dto;

import lombok.Data;

@Data
public class NotificationMessage {
    private String title;
    private String body;
    private String email;
    private Long to;
    private String image;
}
