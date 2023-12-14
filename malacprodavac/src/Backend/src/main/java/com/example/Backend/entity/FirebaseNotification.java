package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "firebaseNotification")
@Getter
@Setter
@NoArgsConstructor
public class FirebaseNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String email;
    private String token;
}
