package com.example.Backend.repository;

import com.example.Backend.entity.Favourite;
import com.example.Backend.entity.Product;
import com.example.Backend.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends ListCrudRepository<Favourite, Long>  {

//    @Query(value = "SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM Favourite f" +
//            " WHERE f.user.id = :userId AND f.product.id = :productId")
//    boolean isFavourite(Long userId, Long productId);

    Optional<Favourite> findByUserIdAndProductId(Long userId, Long productId);

    List<Favourite> findFavouritesByUserIdAndProductIn(Long userId, List<Product> products);

    List<Favourite> findFavouritesByUserId(Long userId);

    Long deleteByUserIdAndProductId(Long userId, Long productId);
}
