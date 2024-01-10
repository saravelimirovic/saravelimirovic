package com.example.Backend.controller;

import com.example.Backend.dto.CardInformationDTO;
import com.example.Backend.dto.CardListForUserDTO;
import com.example.Backend.dto.MyCompanyDTO;
import com.example.Backend.dto.RegisterParam;
import com.example.Backend.entity.CardInformation;
import com.example.Backend.entity.Company;
import com.example.Backend.entity.User;
import com.example.Backend.service.CardInformationService;
import com.example.Backend.service.UserService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/card")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CardInformationController {

    public final CardInformationService cardInformationService;
    private final UserService userService;


    @PostMapping("/add")
    public ResponseEntity<Object> addCard(@RequestBody CardInformationDTO param) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            CardInformation cardInformation = cardInformationService.createCard(param, userId);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAddedCard", cardInformation.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // uzimanje svih kartica jednog usera
    @GetMapping("/allCards")
    public ResponseEntity<?> getAllCardsFromUser() {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<CardListForUserDTO> cardInformation = cardInformationService.getAllCardsFromUser(userId);

//            List<CardInformationDTO> myCards = cardInformation.stream()
//                    .map(myCard -> new CardInformationDTO(myCard))
//                    .toList();

            return new ResponseEntity<>(cardInformation, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long cardId) {
        try {
            CardInformation cardInformation = cardInformationService.getCardById(cardId);

            CardInformationDTO cardInformationDTO = new CardInformationDTO(cardInformation);

            return new ResponseEntity<>(cardInformationDTO, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/deleteCard/{cardId}")
    public ResponseEntity<?> deleteCardById(@PathVariable Long cardId) {
        try {
            Long cardInformation = cardInformationService.deleteCardById(cardId);

            return ResponseEntity.ok(
                    Map.of("Message", cardInformation)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
