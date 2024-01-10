package com.example.Backend.repository;

import com.example.Backend.entity.Cart;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends ListCrudRepository<Cart, Long> {
    Cart findByOwnerIdAndProductIdAndBuyerId(Long ownerId, Long productId, Long buyerId);

    List<Cart> findByOwnerIdAndBuyerId(Long ownerId, Long buyerId);

    void deleteByBuyerIdAndProductId(Long buyerId, Long productId);
    void deleteByBuyerIdAndOwnerId(Long buyerId, Long ownerId);

}
