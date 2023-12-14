package com.example.Backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

import static jakarta.persistence.FetchType.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @Column(name = "id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String credentials;
    @Column(columnDefinition = "TEXT")
    private String token;

    @ManyToOne(fetch = LAZY)
    private User user;

    public Token(String token, String credentials) {
        this.token = token;
        this.credentials = credentials;
    }

    public Token(String token, String credentials, User user) {
        this.token = token;
        this.credentials = credentials;
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Token tokenObj = (Token) obj;

        return Objects.equals(id, tokenObj.id);
    }
}
