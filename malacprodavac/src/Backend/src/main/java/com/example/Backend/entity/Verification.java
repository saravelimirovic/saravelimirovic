package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Entity
@Table(name = "verification")
@Getter
@Setter
@NoArgsConstructor
public class Verification {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String email;
    private String code;

    public Verification(Verification verification) {
        this.id = verification.getId();
        this.email = verification.getEmail();
        this.code = verification.getCode();
    }
}
