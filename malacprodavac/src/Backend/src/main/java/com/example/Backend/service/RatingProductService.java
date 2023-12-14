package com.example.Backend.service;

import com.example.Backend.entity.Favourite;
import com.example.Backend.entity.Product;
import com.example.Backend.entity.RatingProduct;
import com.example.Backend.entity.User;
import com.example.Backend.repository.ProductRepository;
import com.example.Backend.repository.RatingProductRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RatingProductService {

    private final RatingProductRepository ratingProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // dodaj rate producta
    @Transactional
    public RatingProduct saveProductRate(Long userId, Long productId, Integer rate) {
        User user = userRepository.findUserById(userId).orElse(null);
        Product product = productRepository.findProductById(productId).orElse(null);

        RatingProduct ratingProduct = new RatingProduct();
        ratingProduct.setUser(user);
        ratingProduct.setProduct(product);
        ratingProduct.setRate(rate);

        if(user != null && product != null) {
            return ratingProductRepository.save(ratingProduct);
        }

        return null;
    }

    // obrisi rate
    @Transactional
    public Long deleteProductRate(Long userId, Long productId) {
        User user = userRepository.findUserById(userId).orElse(null);
        Product product = productRepository.findProductById(productId).orElse(null);

        if(user != null && product != null) {
            return ratingProductRepository.deleteByUserIdAndProductId(userId, productId);
        }

        return (long)-1;
    }
}
