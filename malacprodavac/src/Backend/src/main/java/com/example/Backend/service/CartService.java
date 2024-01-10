package com.example.Backend.service;

import com.example.Backend.dto.CartDTO;
import com.example.Backend.dto.MyLocationCartDTO;
import com.example.Backend.dto.MyLocationUpdateDTO;
import com.example.Backend.dto.ProductInCartDTO;
import com.example.Backend.entity.Cart;
import com.example.Backend.entity.Product;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.repository.CartRepository;
import com.example.Backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final FileSystemUtil fileSystem;

    public Cart addToCart(CartDTO row, Long buyerId) {
        Cart checkCart = cartRepository.findByOwnerIdAndProductIdAndBuyerId(row.getOwnerId(), row.getProductId(), buyerId);

        if (checkCart == null) {
            Cart cart = new Cart();
            cart.setBuyerId(buyerId);
            cart.setOwnerId(row.getOwnerId());
            cart.setProductId(row.getProductId());
            cart.setQuantity(row.getQuantity());
            cart.setProductTotal(row.getProductTotal());
            cartRepository.save(cart);
            return cart;
        }
        else {
            checkCart.setQuantity(checkCart.getQuantity() + row.getQuantity());
            checkCart.setProductTotal(checkCart.getProductTotal() + row.getProductTotal());
            cartRepository.save(checkCart);
            return checkCart;
        }
    }

    public List<ProductInCartDTO> getProductsInCart(Long buyerId, Long ownerId) {
        List<Cart> list = cartRepository.findByOwnerIdAndBuyerId(ownerId, buyerId);
        List<ProductInCartDTO> finalList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Optional<Product> p = productRepository.findProductById(list.get(i).getProductId());
            finalList.add(new ProductInCartDTO(p.get().getId(),
                                            fileSystem.getImageInBytes(String.valueOf(p.get().getId()), ImageType.PRODUCT),
                                            p.get().getName(),
                                            p.get().getMeasuringUnit().getId(),
                                            list.get(i).getProductTotal(),
                                            list.get(i).getQuantity(),
                                            p.get().getQuantity()));
        }

        return finalList;
    }

    @Transactional
    public boolean deleteProduct(Long buyerId, Long productId) {
        cartRepository.deleteByBuyerIdAndProductId(buyerId, productId);
        return true;
    }

    @Transactional
    public boolean deleteCart(Long buyerId, Long ownerId) {
        cartRepository.deleteByBuyerIdAndOwnerId(buyerId, ownerId);
        return true;
    }
}
