package com.example.Backend.service;

import com.example.Backend.entity.*;
import com.example.Backend.repository.FavouriteRepository;
import com.example.Backend.repository.ProductRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    // vraca korisnike koje ja pratim // novo
    @Transactional(readOnly = true)
    public boolean isFavourite(Long userId, Long productId, Consumer<Favourite> init) {
        Optional<Favourite> favourite = favouriteRepository.findByUserIdAndProductId(userId, productId);

        if (init != null) {
            favourite.ifPresent(init);
        }

        if(favourite.isPresent())
            return true;
        else
            return false;
    }

    @Transactional(readOnly = true)
    public Map<Long, Boolean> getFavourites(Long userId, List<Product> products) {
        List<Favourite> favourites = favouriteRepository.findFavouritesByUserId(userId);

        List<Long> favouritesProductIds = favourites.stream()
                .map(favourite -> favourite.getProduct().getId())
                .toList();

        Map<Long, Boolean> favourtesMap = new HashMap<>();

        for (Product product : products) {
            boolean isFavourites = favouritesProductIds.contains(product.getId());
            favourtesMap.put(product.getId(), isFavourites);
        }

        return favourtesMap;
    }

    // dodaj product u favourite
    @Transactional
    public Favourite saveFavourite(Long userId, Long productId) {
        User user = userRepository.findUserById(userId).orElse(null);
        Product product = productRepository.findProductById(productId).orElse(null);

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);

        if(user != null && product != null) {
            return favouriteRepository.save(favourite);
        }

        return null;
    }

    // izbaci iz favourite
    @Transactional
    public Long deleteFavourite(Long userId, Long productId) {
        User user = userRepository.findUserById(userId).orElse(null);
        Product product = productRepository.findProductById(productId).orElse(null);

        if(user != null && product != null) {
            return favouriteRepository.deleteByUserIdAndProductId(userId, productId);
        }

        return (long)-1;
    }
}
