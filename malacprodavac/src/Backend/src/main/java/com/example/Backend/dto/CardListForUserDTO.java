package com.example.Backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardListForUserDTO {
    private Long id;
    private String nameOnCard;

    public CardListForUserDTO(Long id, String nameOnCard) {
        this.id = id;
        this.nameOnCard = nameOnCard;
    }
}
