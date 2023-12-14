package com.example.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "cardInformation")
@Getter
@Setter
@NoArgsConstructor
public class CardInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String nameOnCard;
    private String cardNumber;
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date cardExpiration;
    private String securityCode;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "id")
    private User user;


    //  -------------------------------
}
