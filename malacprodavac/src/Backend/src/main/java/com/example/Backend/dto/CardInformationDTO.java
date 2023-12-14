package com.example.Backend.dto;


import com.example.Backend.entity.CardInformation;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

// prihvatam
@Getter
public class CardInformationDTO {
    private Long id;
    private String nameOnCard;
    private String cardNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private java.util.Date cardExpiration;
    private String securityCode;

    public CardInformationDTO() {
    }

    public CardInformationDTO(CardInformation cardInformation) {
        this.id = cardInformation.getId();
        this.nameOnCard = cardInformation.getNameOnCard();
        this.cardNumber = cardInformation.getCardNumber();
        this.cardExpiration = cardInformation.getCardExpiration();
        this.securityCode = cardInformation.getSecurityCode();
    }
}
