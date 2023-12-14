package com.example.Backend.repository;

import com.example.Backend.entity.Post;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends ListCrudRepository<Post, Long> {
    Optional<Post> findPostById(Long id);
}
