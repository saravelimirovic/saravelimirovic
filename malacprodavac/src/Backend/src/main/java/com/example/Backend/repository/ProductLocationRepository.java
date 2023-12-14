package com.example.Backend.repository;

import com.example.Backend.entity.ProductLocation;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProductLocationRepository extends ListCrudRepository<ProductLocation, Long> {

    List<ProductLocation> findByStreetId(Long streetId);
}
