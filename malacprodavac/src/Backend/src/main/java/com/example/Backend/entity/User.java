package com.example.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Integer age;
    private String phoneNumber;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streetId", referencedColumnName = "id")
    private Street street;
    private String streetNumber;
//    private String image;
    private double longitude;
    private double latitude;
    private String postalCode;
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_roles")
    @Column(nullable = false)
    private Set<UserRoles> userRoles;

    //  -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<Post> posts;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", fetch = LAZY, orphanRemoval = true)
    private List<Order> customers;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", fetch = LAZY, orphanRemoval = true)
    private List<Order> owners;

    @JsonIgnore
    @OneToMany(mappedBy = "deliverer", fetch = LAZY, orphanRemoval = true)
    private List<Delivery> deliveries;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<Following> usersWhoFollows;

    @JsonIgnore
    @OneToMany(mappedBy = "userFollowing", fetch = LAZY, orphanRemoval = true)
    private List<Following> usersFollowing;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<Like> usersWhoLikePosts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<Favourite> usersHaveFavourites;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<Company> companies;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<RatingProduct> ratingProducts;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<RatingUser> usersWhoRate;

    @JsonIgnore
    @OneToMany(mappedBy = "userRating", fetch = LAZY, orphanRemoval = true)
    private List<RatingUser> ratingUsers;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<CardInformation> cards;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = LAZY, orphanRemoval = true)
    private List<CommentProduct> productComments;





    public User(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.tokens = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public Double getAverageRating() {
        if (ratingProducts.isEmpty()) {
            return 0.0; // ili neku podrazumevanu vrednost
        }

        double sum = ratingProducts.stream()
                .mapToInt(RatingProduct::getRate).sum();
        return sum / ratingProducts.size();
    }
}
