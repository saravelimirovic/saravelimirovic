package com.example.Backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemPair {
//    private Order order;
    private Long orderId;
    private Long productId;
    private Double quantity;

//    public OrderItemPair(Order order, Long idProduct, Double quantity) {
//        this.order = order;
//        this.productId = idProduct;
//        this.quantity = quantity;
//    }

        public OrderItemPair(Long orderId, Long idProduct, Double quantity) {
        this.orderId = orderId;
        this.productId = idProduct;
        this.quantity = quantity;
    }
}
