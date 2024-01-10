package com.example.Backend.repository;

import com.example.Backend.dto.MyOrderDTO;
import com.example.Backend.entity.Delivery;
import com.example.Backend.entity.Order;
import com.example.Backend.entity.StatusOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends ListCrudRepository<Order, Long> {
    List<Order> findOrdersByCustomerId(Long customerId);

    List<Order> findOrdersByOwnerId(Long ownerId);

    Optional<Order> findOrderById(Long orderId);

    List<Order> findOrderByOwnerIdAndStatus(Long userId, StatusOrder statusOrder);

    List<Order> findOrdersByDeliveryInAndStatus(List<Delivery> deliveries, StatusOrder statusOrder );
    List<Order> findOrdersByDeliveryIn(List<Delivery> deliveries);

}
