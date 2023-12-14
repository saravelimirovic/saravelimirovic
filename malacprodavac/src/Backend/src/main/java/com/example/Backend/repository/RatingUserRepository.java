package com.example.Backend.repository;

import com.example.Backend.entity.RatingUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RatingUserRepository extends ListCrudRepository<RatingUser, Long> {

    Long deleteByUserIdAndAndUserRatingId(Long userId, Long userRatingId);
}
