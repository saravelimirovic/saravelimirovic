package com.example.Backend.service;

import com.example.Backend.entity.Order;
import com.example.Backend.entity.OrderItem;
import com.example.Backend.entity.OrderItemPair;
import com.example.Backend.entity.Product;
import com.example.Backend.repository.OrderItemRepository;
import com.example.Backend.repository.OrderRepository;
import com.example.Backend.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<OrderItem> getOrderItemsByOrderIdIn(List<Long> orderIds, Consumer<OrderItem> init) {
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderIdIn(orderIds);

        if(init != null) {
            orderItems.forEach(init);
        }

        return orderItems;
    }

    public List<OrderItem> getOrderItemsByOrderId(Long orderId, Consumer<OrderItem> init) {
        List<OrderItem> orderItems = orderItemRepository.findOrderItemsByOrderId(orderId);

        if(init != null) {
            orderItems.forEach(init);
        }

        return orderItems;
    }

    @Transactional(readOnly = true)
    public Map<Long, Double> getTotalPrice(List<Long> orderIds) {
        // uzima jedinstvene orderId-jeve, u slucaju da se desi da imamo vise istih orderId-jeva
        List<OrderItem> groupByOrderItems = orderItemRepository.findDistinctByOrderIdIn(orderIds);

        Map<Long, Double> totalPriceMap = new HashMap<>();

        for (OrderItem orderItem : groupByOrderItems) {
            List<OrderItem> orderItemsByOrderId = orderItemRepository.findOrderItemsByOrderId(orderItem.getOrder().getId());
            List<Product> productsOfOneOrder = orderItemsByOrderId.stream()
                    .map(OrderItem::getProduct)
                    .toList();
            Double totalPrice = sumPrices(productsOfOneOrder);

            totalPriceMap.put(orderItem.getId(), totalPrice);
        }

        return totalPriceMap;
    }

    @Transactional(readOnly = true)
    public Double sumPrices(List<Product> products) {
        Double total = 0.0;

        for (Product product : products) {
            total += product.getPrice();
        }

        return total;
    }
}
