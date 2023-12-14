package com.example.Backend.service;

import com.example.Backend.entity.Following;
import com.example.Backend.entity.RatingUser;
import com.example.Backend.entity.User;
import com.example.Backend.repository.RatingUserRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RatingUserService {

    private final RatingUserRepository ratingUserRepository;
    private final UserRepository userRepository;

    // dodaj rate
    @Transactional
    public RatingUser saveUserRate(Long userId, Long userId1, Integer rate) {
        User user = userRepository.findUserById(userId).orElse(null);
        User user1 = userRepository.findUserById(userId1).orElse(null);

        RatingUser ratingUser = new RatingUser();
        ratingUser.setUser(user);
        ratingUser.setUserRating(user1);
        ratingUser.setRate(rate);

        if(user != null && user1 != null) {
            return ratingUserRepository.save(ratingUser);
        }

        return null;
    }

    // obrisi rejt
    @Transactional
    public Long deleteUserRate(Long userId, Long userId1) {
        User user = userRepository.findUserById(userId).orElse(null);
        User user1 = userRepository.findUserById(userId1).orElse(null);

        if(user != null && user1 != null) {
            return ratingUserRepository.deleteByUserIdAndAndUserRatingId(userId, userId1);

        }

        return (long)-1;
    }
}
