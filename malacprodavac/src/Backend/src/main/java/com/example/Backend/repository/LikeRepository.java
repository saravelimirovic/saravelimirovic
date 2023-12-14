package com.example.Backend.repository;

import com.example.Backend.entity.Like;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends ListCrudRepository<Like, Long> {

    Long deleteByUserIdAndPostId(Long userId, Long postId);
}
