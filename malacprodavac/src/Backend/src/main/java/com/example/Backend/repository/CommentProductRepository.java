package com.example.Backend.repository;

import com.example.Backend.entity.CommentProduct;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentProductRepository extends ListCrudRepository<CommentProduct, Long> {

    List<CommentProduct> findCommentProductByProductId(Long productId);
}
