package com.example.Backend.controller;

import com.example.Backend.entity.RatingProduct;
import com.example.Backend.entity.RatingUser;
import com.example.Backend.service.RatingUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/ratingUser")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RatingUserController {

    private final RatingUserService ratingUserService;

    // dodaj rate za usera - userId za koji sam dodao
    @PostMapping("/addRate")
    public ResponseEntity<?> saveUserRate(@RequestParam Long userId1, @RequestParam Integer rate) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            RatingUser response = ratingUserService.saveUserRate(userId, userId1, rate);

            if(response != null)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("User you want to rate does not exists.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // obrisi rejt - nez dal treba da postoji
    @DeleteMapping("deleteRate/{userId1}")
    public ResponseEntity<?> deleteFollow(@PathVariable Long userId1) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Long response = ratingUserService.deleteUserRate(userId, userId1);

            if(response != -1)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("User probably do not exists or you've never rated them.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
