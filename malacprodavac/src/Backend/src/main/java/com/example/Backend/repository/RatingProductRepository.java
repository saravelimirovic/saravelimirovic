package com.example.Backend.repository;

import com.example.Backend.entity.Product;
import com.example.Backend.entity.RatingProduct;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingProductRepository extends ListCrudRepository<RatingProduct, Long> {
    Long deleteByUserIdAndProductId(Long userId, Long productId);

    List<RatingProduct> findRatingProductByProductIn(List<Product> products);
}
