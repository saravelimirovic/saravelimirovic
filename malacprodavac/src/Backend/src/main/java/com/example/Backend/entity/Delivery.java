package com.example.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@NoArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routeId", referencedColumnName = "id")
    private Route route;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivererId", referencedColumnName = "id")
    private User deliverer;
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date deliveryDate;

    //  -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, orphanRemoval = true, fetch = LAZY)
    private List<Order> orders;
}
