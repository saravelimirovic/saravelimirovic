package com.example.Backend.controller;

import com.example.Backend.dto.CardInformationDTO;
import com.example.Backend.dto.CommentProductDTO;
import com.example.Backend.dto.HomePageProductDTO;
import com.example.Backend.dto.ProductCommentDTO;
import com.example.Backend.entity.CardInformation;
import com.example.Backend.entity.CommentProduct;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.service.CommentProductService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/commentProduct")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommentProductController {

    private final CommentProductService commentProductService;

    private final FileSystemUtil fileSystem;


    @PostMapping("/add")
    public ResponseEntity<Object> addComment(@RequestBody CommentProductDTO param) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            CommentProduct commentProduct = commentProductService.createComment(userId, param);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAddedComment", commentProduct.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // uzimanje komentara jednog proizvoda
    @GetMapping("/getAllForProduct/{productId}")
    public ResponseEntity<Object> getAllCommentsForProductId(@PathVariable Long productId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<CommentProduct> commentProductList = commentProductService.getAllCommentsByProductId(productId, c -> {
                Hibernate.initialize(c.getUser());
            });

            List<ProductCommentDTO> productComments = commentProductList.stream()
                    .map(comment -> new ProductCommentDTO(comment,
                            fileSystem.getImageInBytes(String.valueOf(comment.getUser().getId()), ImageType.USER)))
                    .toList();

            return new ResponseEntity<>(productComments, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
