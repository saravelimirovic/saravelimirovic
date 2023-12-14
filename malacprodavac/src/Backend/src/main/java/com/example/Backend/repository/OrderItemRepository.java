package com.example.Backend.repository;

import com.example.Backend.entity.OrderItem;
import com.example.Backend.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends ListCrudRepository<OrderItem, Long> {

    List<OrderItem> findOrderItemsByOrderIdIn(List<Long> orderId);
    List<OrderItem> findOrderItemsByOrderId(Long orderId);

    @Query("SELECT DISTINCT oi " +
            "FROM OrderItem oi " +
            "WHERE oi.order.id IN :orderIds")
    List<OrderItem> findDistinctByOrderIdIn(@Param("orderIds") List<Long> orderIds);
}
