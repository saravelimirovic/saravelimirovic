package com.example.Backend.service;

import com.example.Backend.dto.CommentProductDTO;
import com.example.Backend.entity.CommentProduct;
import com.example.Backend.entity.Product;
import com.example.Backend.entity.User;
import com.example.Backend.repository.CommentProductRepository;
import com.example.Backend.repository.ProductRepository;
import com.example.Backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentProductService {

    private final CommentProductRepository commentProductRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CommentProduct createComment(Long userId, CommentProductDTO param) {
        User user = userRepository.findUserById(userId).orElse(null);
        Product product = productRepository.findProductById(param.getProductId()).orElse(null);

        if(product == null)
            throw new IllegalArgumentException("Product with this id does not exists");

        CommentProduct commentProduct = new CommentProduct();
        commentProduct.setUser(user);
        commentProduct.setProduct(product);
        commentProduct.setText(param.getText());
        commentProduct.setDate(new Date());

        return commentProductRepository.save(commentProduct);
    }

    @Transactional(readOnly = true)
    public List<CommentProduct> getAllCommentsByProductId(Long productId, Consumer<CommentProduct> init) {
        List<CommentProduct> commentProductList = commentProductRepository.findCommentProductByProductId(productId);

        if(init != null) {
            commentProductList.forEach(init);
        }

        return commentProductList;
    }
}
