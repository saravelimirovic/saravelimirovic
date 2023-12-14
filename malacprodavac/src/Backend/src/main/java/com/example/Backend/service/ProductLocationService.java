package com.example.Backend.service;

import com.example.Backend.entity.ProductLocation;
import com.example.Backend.repository.ProductLocationRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ProductLocationService {

    private final ProductLocationRepository productLocationRepository;


    public List<ProductLocation> getProductLocationsByStreetId(Long streetId, Consumer<ProductLocation> init) {
        List<ProductLocation> productLocations = productLocationRepository.findByStreetId(streetId);

        if (init != null) {
            productLocations.forEach(init);
        }

        return productLocations;
    }
}
