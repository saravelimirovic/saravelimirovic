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
@Table(name = "city")
@Getter
@Setter
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;
    private String name;

    //  -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "city", fetch = LAZY, orphanRemoval = true)
    private List<Street> streets;

    @JsonIgnore
    @OneToMany(mappedBy = "startPointCity", fetch = LAZY, orphanRemoval = true)
    private List<Route> startRoutes;

    @JsonIgnore
    @OneToMany(mappedBy = "endPointCity", fetch = LAZY, orphanRemoval = true)
    private List<Route> endRoutes;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
