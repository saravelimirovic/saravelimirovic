package com.example.Backend.controller;

import com.example.Backend.entity.Favourite;
import com.example.Backend.service.FavouriteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/favourite")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FavouriteController {

    private final FavouriteService favouriteService;

    // dodaj product u favourite (sacuvaj u favourite) - productId koji sam dodao
    @PostMapping("/addFavourite/{productId}")
    public ResponseEntity<?> saveFavourite(@PathVariable Long productId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Favourite response = favouriteService.saveFavourite(userId, productId);

            if(response != null)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("Product do not exist.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // izbaci iz favourite
    @DeleteMapping("deleteFavourite/{productId}")
    public ResponseEntity<?> deleteFavourite(@PathVariable Long productId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Long response = favouriteService.deleteFavourite(userId, productId);

            if(response != -1)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("This product do not exists or it was never in favourite.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
