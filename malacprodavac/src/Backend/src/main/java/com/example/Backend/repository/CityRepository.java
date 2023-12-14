package com.example.Backend.repository;

import com.example.Backend.entity.City;
import com.example.Backend.entity.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends ListCrudRepository<City, Long> {

    Optional<City> findCityByName(String city);
    Optional<City> findCityById(Long id);
}
