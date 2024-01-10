package com.example.Backend.controller;

import com.example.Backend.dto.CartDTO;
import com.example.Backend.dto.ProductInCartDTO;
import com.example.Backend.entity.Cart;
import com.example.Backend.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/cart")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CartController {

    CartService cartService;

    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@RequestBody CartDTO cart) {
        Long userId = getUserDetails().getId(); // kupljenje iz tokena

        Cart response = cartService.addToCart(cart, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getProductsInCart/{ownerId}")
    public ResponseEntity<?> getProducts(@PathVariable Long ownerId) {
        Long buyerId = getUserDetails().getId(); // kupljenje iz tokena

        List<ProductInCartDTO> list = cartService.getProductsInCart(buyerId, ownerId);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("/deleteProductFromCart/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        Long buyerId = getUserDetails().getId(); // kupljenje iz tokena
        cartService.deleteProduct(buyerId, productId);

        return ResponseEntity.ok(
                Map.of("Message", true)
        );
    }

    @DeleteMapping("/deleteCart/{ownerId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long ownerId) {
        Long buyerId = getUserDetails().getId(); // kupljenje iz tokena
        cartService.deleteCart(buyerId, ownerId);

        return ResponseEntity.ok(
                Map.of("Message", true)
        );
    }
}