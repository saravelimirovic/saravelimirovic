package com.example.Backend.controller;

import com.example.Backend.dto.DeliveryDTO;
import com.example.Backend.dto.DeliveryResDTO;
import com.example.Backend.entity.*;
import com.example.Backend.service.DeliveryService;
import com.example.Backend.service.OrderService;
import com.example.Backend.service.ProductService;
import com.example.Backend.service.RouteService;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/delivery")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final RouteService routeService;
    private final OrderService orderService;


    // dostavljac kreira svoju rutu
    @PostMapping("/add")
    public ResponseEntity<Object> addDelivery(@RequestBody DeliveryDTO param) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            Delivery delivery = deliveryService.createDelivery(param, userId);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAdded", delivery.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // vraca rute, tj. pretraga ruta (za poslate gradove trazi rute)
    @PostMapping("/listOfRoutes/{pageIndex}/{pageSize}")
    public ResponseEntity<Object> getListOfRoutes(@RequestBody DeliveryDTO param, @PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            // vraca rute koje zadovoljavaju kriterijum
            List<Route> routes = routeService.getRoutesByGivenRoutes(param, r -> {
                Hibernate.initialize(r.getStartPointCity());
                Hibernate.initialize(r.getEndPointCity());
            });

            // vraca info o delivery za te rute
            List<Delivery> deliveries = deliveryService.getDeliveriesByRoutes(routes, d -> {
                Hibernate.initialize(d.getDeliverer());
                Hibernate.initialize(d.getRoute());
            });

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > deliveries.size()) {
                return new ResponseEntity<>("No deliveries available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, deliveries.size()); // (pageIndex + 1) * pageSize
            List<Delivery> paginatedDeliveries = deliveries.subList(start, end);

            List<DeliveryResDTO> deliveriesDto = paginatedDeliveries.stream()
                    .map(delivery -> new DeliveryResDTO(delivery))
                    .toList();


            return new ResponseEntity<>(deliveriesDto, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // vraca moje objavljene rute koje su zakazane (za dostavljaca)
    @GetMapping("/routesScheduled/{pageIndex}/{pageSize}")
    public ResponseEntity<Object> getListOfRoutesScheduled(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Delivery> deliveries1 = deliveryService.getDeliveriesByUserId(userId, d -> {
                Hibernate.initialize(d.getRoute());
            });

            // vraca sa statusom OUT_FOR_DELIVERY
            List<Order> orders = orderService.getOrdersByDeliveriesAndStatus(deliveries1, o -> {
                Hibernate.initialize(o.getDelivery());
                Hibernate.initialize(o.getDelivery().getRoute());
            });

            List<Delivery> deliveries = orders.stream()
                    .map(Order::getDelivery)
                    .toList();

            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > deliveries.size()) {
                return new ResponseEntity<>("No deliveries available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, deliveries.size()); // (pageIndex + 1) * pageSize
            List<Delivery> paginatedDeliveries = deliveries.subList(start, end);

            List<DeliveryResDTO> deliveriesDto = paginatedDeliveries.stream()
                    .map(delivery -> new DeliveryResDTO(delivery))
                    .toList();


            return new ResponseEntity<>(deliveriesDto, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // vraca moje objavljene rute koje nisu zakazane (za dostavljaca)
    @GetMapping("/routesNotScheduled/{pageIndex}/{pageSize}")
    public ResponseEntity<Object> getListOfRoutesNotScheduled(@PathVariable int pageIndex, @PathVariable int pageSize) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            // ovo su svi deliveries jednog usera
            List<Delivery> deliveries1 = deliveryService.getDeliveriesByUserId(userId, d -> {
                Hibernate.initialize(d.getRoute());
            });

            // vraca sve koji su vezani za deliveri
            List<Order> orders = orderService.getOrdersByDeliveries(deliveries1, o -> {
                Hibernate.initialize(o.getDelivery());
                Hibernate.initialize(o.getDelivery().getRoute());
            });

            // ovde su deliveries koji su vezani za order
            List<Long> deliveries = orders.stream()
                    .map(order -> order.getDelivery().getId())
                    .toList();

            // ovde su ostali deliveries
            List<Delivery> filteredDeliveries = deliveryService.getDeliveriesNotIn(deliveries, d -> {
                Hibernate.initialize(d.getRoute());
            });


            // Paginacija
            int start = pageIndex * pageSize - pageSize;
            if(start > filteredDeliveries.size()) {
                return new ResponseEntity<>("No deliveries available for that page.", HttpStatus.BAD_REQUEST);
            }
            int end = Math.min(pageIndex * pageSize, filteredDeliveries.size()); // (pageIndex + 1) * pageSize
            List<Delivery> paginatedDeliveries = filteredDeliveries.subList(start, end);

            List<DeliveryResDTO> deliveriesDto = paginatedDeliveries.stream()
                    .map(delivery -> new DeliveryResDTO(delivery))
                    .toList();


            return new ResponseEntity<>(deliveriesDto, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
