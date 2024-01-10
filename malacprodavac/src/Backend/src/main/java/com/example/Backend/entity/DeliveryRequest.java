package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deliveryrequest")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private Long sellerId;
    private Long delivererId;
    private String cityStart;
    private String cityEnd;
    private String date;
    private String time;
}
