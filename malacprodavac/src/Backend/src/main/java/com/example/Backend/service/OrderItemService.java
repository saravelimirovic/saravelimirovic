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
import java.util.stream.Collectors;

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

    public Map<Long, Double> getTotalPrice(List<Long> orderIds) {
        List<OrderItem> orderItems = orderItemRepository.findDistinctByOrderIdIn(orderIds);

        Map<Long, Double> totalPriceMap = orderItems.stream()
                .collect(Collectors.toMap(
                        orderItem -> orderItem.getProduct().getId(),
                        orderItem -> {
                            double price = orderItem.getQuantity() * orderItem.getProduct().getPrice();
                            System.out.println("OrderItem ID: " + orderItem.getId() + ", Calculated Price: " + price);
                            return price;
                        },
                        Double::sum
                ));

        // Dodajte ispisivanje konačnih cena
        System.out.println("Final Total Prices:");

        return totalPriceMap;
    }



    private Double sumPrices(List<OrderItem> orderItems) {
        double sum = orderItems.stream()
                .mapToDouble(oi -> {
                    double quantity = oi.getQuantity();
                    double price = oi.getProduct().getPrice();
                    double total = quantity * price;
                    System.out.println("Quantity: " + quantity + ", Price: " + price + ", Total: " + total);
                    return total;
                })
                .sum();

        System.out.println("Sum: " + sum);

        return sum;
    }

//    @Transactional(readOnly = true)
//    public Double sumPrices(List<Product> products) {
//        Double total = 0.0;
//
//        for (Product product : products) {
//            total += product.getPrice();
//        }
//
//        return total;
//    }
}
