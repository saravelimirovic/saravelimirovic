package com.example.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private User customer;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerId", referencedColumnName = "id")
    private User owner;
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date dateOrderIsMade;
    @Enumerated(STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(STRING)
    private DeliveryMethod deliveryMethod;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryId", referencedColumnName = "id")
    private Delivery delivery;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deliveryStreetId", referencedColumnName = "id")
    private Street deliveryStreet;
    private String deliveryStreetNumber;
    @Enumerated(STRING)
    private StatusOrder status;



    //  -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "order", fetch = LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Double getTotalPrice() {
        if (orderItems == null || orderItems.isEmpty()) {
            return 0.0;
        }

        return orderItems.stream()
                .mapToDouble(orderItem ->
                        orderItem.getQuantity() * orderItem.getProduct().getPrice())
                .sum();
    }

}
