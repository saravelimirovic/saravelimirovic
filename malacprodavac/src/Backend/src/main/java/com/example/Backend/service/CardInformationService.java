package com.example.Backend.service;

import com.example.Backend.dto.CardInformationDTO;
import com.example.Backend.dto.CardListForUserDTO;
import com.example.Backend.entity.CardInformation;
import com.example.Backend.entity.User;
import com.example.Backend.repository.CardInformationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CardInformationService {

    public final CardInformationRepository cardInformationRepository;
    public final UserService userService;


    @Transactional
    public CardInformation createCard(CardInformationDTO param, Long userId) {

        CardInformation cardInformation = new CardInformation();
        cardInformation.setNameOnCard(param.getNameOnCard());
        cardInformation.setCardNumber(param.getCardNumber());
        cardInformation.setCardExpiration(param.getCardExpiration());
        cardInformation.setSecurityCode(param.getSecurityCode());
        User user = userService.getUserById(userId, null);
        cardInformation.setUser(user);

        return cardInformationRepository.save(cardInformation);
    }

    @Transactional(readOnly = true)
    public List<CardListForUserDTO> getAllCardsFromUser(Long userId) {
        List<CardListForUserDTO> cardInformation = cardInformationRepository.findCardInformationByUserId(userId);

        return cardInformation;
    }

    @Transactional(readOnly = true)
    public CardInformation getCardById(Long cardId) {
        CardInformation cardInformation = cardInformationRepository.findCardInformationById(cardId);

        return cardInformation;
    }

    @Transactional
    public Long deleteCardById(Long cardId) {
        Long cardInformation = cardInformationRepository.deleteCardInformationById(cardId);

        return cardInformation;
    }
}
