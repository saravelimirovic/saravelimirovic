package com.example.Backend.controller;

import com.example.Backend.entity.Following;
import com.example.Backend.entity.Like;
import com.example.Backend.service.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/like")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class LikeController {

    private final LikeService likeService;

    // lajkuj post (sacuva like) - postId posta koji sam lajkovao
    @PostMapping("/addLike/{postId}")
    public ResponseEntity<?> saveLike(@PathVariable Long postId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Like response = likeService.saveLike(userId, postId);

            if(response != null)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("Post do not exists.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // skini lajk (brise lajk)
    @DeleteMapping("deleteLike/{postId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long postId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Long response = likeService.deleteLike(userId, postId);

            if(response != -1)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("This post do not exists or it was never liked.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
