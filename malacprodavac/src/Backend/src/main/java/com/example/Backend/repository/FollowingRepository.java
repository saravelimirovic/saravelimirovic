package com.example.Backend.repository;

import com.example.Backend.entity.Following;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowingRepository extends ListCrudRepository<Following, Long> {

    List<Following> findByUserId(Long userId);

    List<Following> findByUserFollowingId(Long userId);

    Long deleteByUserIdAndAndUserFollowingId(Long userId, Long userFollowingId);

    Following findByUserIdAndUserFollowingId(Long userId, Long userIfFollowId);
}
