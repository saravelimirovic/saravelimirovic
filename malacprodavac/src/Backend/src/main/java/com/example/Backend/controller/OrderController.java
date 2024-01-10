package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.service.OrderItemService;
import com.example.Backend.service.OrderService;
import com.example.Backend.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/order")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private final FileSystemUtil fileSystem;

    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orders) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena
            List<OrderItem> addedOrders = orderService.createOrders(orders, userId);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(orders);
            System.out.println(json);
            // nez sta da vracam ovde
            return new ResponseEntity<>(addedOrders, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // vraca ordere od kupca (sve info)
    @GetMapping("/myOrders/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getOrdersByCustomerId(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Order> orders = orderService.getOrdersByCustomerId(userId, o -> {
                Hibernate.initialize(o.getDelivery());
                Hibernate.initialize(o.getDeliveryStreet());
            });

            if (orders.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not have any order yet.", HttpStatus.NOT_FOUND);

            List<Long> orderIds = orders.stream()
                    .map(order -> order.getId())
                    .toList();

            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderIdIn(orderIds, oi -> {
                Hibernate.initialize(oi.getProduct());
                Hibernate.initialize(oi.getProduct().getCompany());
                Hibernate.initialize(oi.getProduct().getCompany().getUser());
            });

            List<MyOrderDTO> myOrders = orderItems.stream()
                    .map(orderItem -> new MyOrderDTO(orderItem.getOrder(), orderItem.getProduct(), orderItem.getQuantity()))
                    .toList();

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > myOrders.size()) {
                return new ResponseEntity<>("No orders available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, myOrders.size()); // (pageIndex + 1) * pageSize
            List<MyOrderDTO> paginatedOrders = myOrders.subList(start, end);


            return new ResponseEntity<>(paginatedOrders, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

// --------------------
    // lista ordera (proizvodjac)
    @GetMapping("/listOfOrders/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getListOfOrders(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Order> orders = orderService.getOrdersByOwnerId(userId);

            if (orders.isEmpty()) {
                return new ResponseEntity<>("User with id=" + userId + " does not have any order yet.", HttpStatus.NOT_FOUND);
            }

            List<OrdersListDTO> listOfOrders = new ArrayList<>();

            for (Order order : orders) {
                Double totalPrice = order.getTotalPrice(); // koristi novu metodu za ukupnu cenu

                OrdersListDTO ordersListDTO = new OrdersListDTO(order, totalPrice);
                listOfOrders.add(ordersListDTO);
            }

            List<OrdersListDTO> distinctOrders = listOfOrders.stream()
                    .collect(Collectors.toMap(
                            OrdersListDTO::getOrderId,  // Ključ je orderID
                            Function.identity(),
                            (existing, replacement) -> existing  // Ako se pojavi duplikat, zadrži original
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > distinctOrders.size()) {
                return new ResponseEntity<>("No orders available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, distinctOrders.size()); // (pageIndex + 1) * pageSize
            List<OrdersListDTO> paginatedOrders = distinctOrders.subList(start, end);

            return new ResponseEntity<>(paginatedOrders, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // lista proizvoda unutar ordera (proizvodjac)
    @GetMapping("/productsInOrder/{orderId}")
    public ResponseEntity<?> getProductsInOrder(@PathVariable Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId, oi -> {
                Hibernate.initialize(oi.getProduct());
            });

            List<ProductInOrderDTO> listOfProducts = orderItems.stream()
                    .map(orderItem -> new ProductInOrderDTO(orderItem.getProduct(), orderItem.getQuantity(),
                            fileSystem.getImageInBytes(String.valueOf(orderItem.getProduct().getId()), ImageType.PRODUCT)))
                    .toList();

            return new ResponseEntity<>(listOfProducts, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // vraca info o korisniku ciji je order (proizvodjacu)
    @GetMapping("/userInOrder/{orderId}")
    public ResponseEntity<?> getUserInOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId, o -> {
                Hibernate.initialize(o.getCustomer());
            });

            UserInfoOrderDTO userIn = new UserInfoOrderDTO(order);

            return new ResponseEntity<>(userIn, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // lista ordera (proizvodjac) -> ali sa statusom READY_FOR_DELIVERY (za koji jos nije pronadjen deliverer)
    @GetMapping("/listOfOrdersReady/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getListOfOrdersReadyForDelivery(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Order> orders = orderService.getOrdersByOwnerIdAndReadyForDelivery(userId, StatusOrder.READY_FOR_DELIVERY, o -> {
                Hibernate.initialize(o.getCustomer());
                Hibernate.initialize(o.getDelivery());
                Hibernate.initialize(o.getDeliveryStreet());
            });

            if (orders.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not have any order yet.", HttpStatus.NOT_FOUND);

            List<Long> orderIds = orders.stream()
                    .map(Order::getId)
                    .toList();

            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderIdIn(orderIds, oi -> {
                Hibernate.initialize(oi.getProduct());
            });

            Map<Long, Double> totalPrices = orderItemService.getTotalPrice(orderIds);

            List<OrdersListDTO> listOfOrders = orderItems.stream()
                    .map(orderItem -> new OrdersListDTO(orderItem.getOrder(), totalPrices.getOrDefault(orderItem.getOrder().getId(), 0.0)))
                    .toList();


            List<OrdersListDTO> distinctOrders = listOfOrders.stream()
                    .collect(Collectors.toMap(
                            OrdersListDTO::getOrderId,  // Ključ je orderID
                            Function.identity(),
                            (existing, replacement) -> existing  // Ako se pojavi duplikat, zadrži original
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > distinctOrders.size()) {
                return new ResponseEntity<>("No orders available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, distinctOrders.size()); // (pageIndex + 1) * pageSize
            List<OrdersListDTO> paginatedOrders = distinctOrders.subList(start, end);

            return new ResponseEntity<>(paginatedOrders, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // lista ordera (proizvodjac) -> ali sa statusom OUT_FOR_DELIVERY (za koji je pronadjen deliverer)
    @GetMapping("/listOfOrdersOut/{pageIndex}/{pageSize}")
    public ResponseEntity<?> getListOfOrdersOutForDelivery(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Order> orders = orderService.getOrdersByOwnerIdAndReadyForDelivery(userId, StatusOrder.OUT_FOR_DELIVERY, o -> {
                Hibernate.initialize(o.getCustomer());
                Hibernate.initialize(o.getDelivery());
                Hibernate.initialize(o.getDeliveryStreet());
            });

            if (orders.size() == 0)
                return new ResponseEntity<>("User with id=" + userId + " does not have any order yet.", HttpStatus.NOT_FOUND);

            List<Long> orderIds = orders.stream()
                    .map(Order::getId)
                    .toList();

            List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderIdIn(orderIds, oi -> {
                Hibernate.initialize(oi.getProduct());
            });

            Map<Long, Double> totalPrices = orderItemService.getTotalPrice(orderIds);

            List<OrdersListDTO> listOfOrders = orderItems.stream()
                    .map(orderItem -> new OrdersListDTO(orderItem.getOrder(), totalPrices.getOrDefault(orderItem.getOrder().getId(), 0.0)))
                    .toList();


            List<OrdersListDTO> distinctOrders = listOfOrders.stream()
                    .collect(Collectors.toMap(
                            OrdersListDTO::getOrderId,  // Ključ je orderID
                            Function.identity(),
                            (existing, replacement) -> existing  // Ako se pojavi duplikat, zadrži original
                    ))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > distinctOrders.size()) {
                return new ResponseEntity<>("No orders available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, distinctOrders.size()); // (pageIndex + 1) * pageSize
            List<OrdersListDTO> paginatedOrders = distinctOrders.subList(start, end);

            return new ResponseEntity<>(paginatedOrders, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
//  --------------------------------------

    // vezuje delivery za order
    @PostMapping("/updateDelivery/{orderId}/{deliveryId}")
    public ResponseEntity<?> updateDelivery(@PathVariable Long deliveryId, @PathVariable Long orderId) {
        try {
            Order order = orderService.updateDelivery(orderId, deliveryId);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedOrder", order.getId())
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // deliverer menja status ordera
    @PostMapping("/updateStatus/{orderId}/{status}")
    public ResponseEntity<?> updateStatusByDeliverer(@PathVariable Long orderId, @PathVariable String status) {
        try {
            StatusOrder statusOrder = StatusOrder.getById(status);

            Order order = orderService.updateStatus(orderId, statusOrder);


            return ResponseEntity.ok(
                    Map.of("Message", 1, "idUpdatedOrder", order.getId())
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
