package com.example.Backend.controller;

import com.example.Backend.entity.Favourite;
import com.example.Backend.entity.RatingProduct;
import com.example.Backend.service.RatingProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/ratingProduct")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RatingProductController {

    private final RatingProductService ratingProductService;

    // dodaj rate za proizvod - productId za koji sam dodao
    @PostMapping("/addRate")
    public ResponseEntity<?> saveLike(@RequestParam Long productId, @RequestParam Integer rate) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            RatingProduct response = ratingProductService.saveProductRate(userId, productId, rate);

            if(response != null)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("Product do not exist.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // obrisi rate, nez dal treba da postoji ??
    @DeleteMapping("deleteRate/{productId}")
    public ResponseEntity<?> deleteRate(@PathVariable Long productId) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Long response = ratingProductService.deleteProductRate(userId, productId);

            if(response != -1)
                return new ResponseEntity<>(response, HttpStatus.OK);
            else
                return new ResponseEntity<>("This product do not exists or it was never in favourite.", HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
