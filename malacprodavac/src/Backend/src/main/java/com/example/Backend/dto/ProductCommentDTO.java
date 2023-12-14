package com.example.Backend.dto;

import com.example.Backend.entity.CommentProduct;
import com.example.Backend.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

// vracam
@Getter
@Setter
public class ProductCommentDTO {
    private Long commentId;
    private byte[] image;
    private String fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", timezone = "UTC")
    private java.util.Date date;
    private String text;

    public ProductCommentDTO(CommentProduct commentProduct, byte[] img) {
        this.commentId = commentProduct.getId();

        User user = commentProduct.getUser();
        this.fullName = user.getFirstName() + " " + user.getLastName();

        this.date = commentProduct.getDate();
        this.text = commentProduct.getText();

        this.image = img;
    }
}
