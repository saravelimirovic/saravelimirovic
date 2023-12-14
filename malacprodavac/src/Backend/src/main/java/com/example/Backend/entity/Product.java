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
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String name;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId", referencedColumnName = "id")
    private Company company;
    @Enumerated(STRING)
    private MeasuringUnit measuringUnit;
    private Double quantity;
    private Double price;
    @Enumerated(STRING)
    private ProductCategory productCategory;
    private String image;
    private String description;


    //  -------------------------------

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = LAZY, orphanRemoval = true)
    private List<Favourite> productFavourite;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = LAZY, orphanRemoval = true)
    private List<ProductLocation> productLocations;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = LAZY, orphanRemoval = true)
    private List<RatingProduct> ratingProducts;

    @JsonIgnore
    @OneToMany(mappedBy = "product", fetch = LAZY, orphanRemoval = true)
    private List<CommentProduct> commentProducts;


    //  -------------------------------

    public Double getAverageRating() {
        if (ratingProducts.isEmpty()) {
            return 0.0; // ili neku podrazumevanu vrednost
        }

        double sum = ratingProducts.stream()
                .mapToInt(RatingProduct::getRate).sum();
        return sum / ratingProducts.size();
    }

    public Integer getTotalCountRating() {

        return ratingProducts.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
