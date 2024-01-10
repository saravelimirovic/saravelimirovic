package com.example.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "street")
@Getter
@Setter
@NoArgsConstructor
public class Street {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cityId", referencedColumnName = "id")
    private City city;
    private String name;

    //  -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "street", fetch = LAZY, orphanRemoval = true)
    private List<User> users;

    @JsonIgnore
    @OneToMany(mappedBy = "street", fetch = LAZY, orphanRemoval = true)
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "deliveryStreet", fetch = LAZY, orphanRemoval = true)
    private List<Order> orders;

    @JsonIgnore
    @OneToMany(mappedBy = "street", fetch = LAZY, orphanRemoval = true)
    private List<Company> companies;

    @JsonIgnore
    @OneToMany(mappedBy = "street", fetch = LAZY, orphanRemoval = true)
    private List<ProductLocation> productLocations;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Street street = (Street) o;
        return Objects.equals(id, street.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
