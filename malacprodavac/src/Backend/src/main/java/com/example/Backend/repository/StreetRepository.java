package com.example.Backend.repository;

import com.example.Backend.entity.City;
import com.example.Backend.entity.Street;
import com.example.Backend.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StreetRepository extends ListCrudRepository<Street, Long>  {

    Optional<Street> findStreetByName(String street);

    Optional<Street> findStreetByNameAndCity(String street, City city);

    Optional<Street> findStreetById(Long streetId);
}
