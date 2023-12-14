package com.example.Backend.repository;

import com.example.Backend.dto.CardListForUserDTO;
import com.example.Backend.entity.CardInformation;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInformationRepository extends ListCrudRepository<CardInformation, Long> {

    List<CardListForUserDTO> findCardInformationByUserId(Long userId);

    CardInformation findCardInformationById(Long cardId);

    Long deleteCardInformationById(Long id);
}
