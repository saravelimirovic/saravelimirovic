package com.example.Backend.service;

import com.example.Backend.dto.HomePageProductDTO;
import com.example.Backend.dto.MyOrderDTO;
import com.example.Backend.dto.OrderDTO;
import com.example.Backend.dto.UpdateProfilePicDTO;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final StreetRepository streetRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeliveryRepository deliveryRepository;

    // svi proizvodi od jednog ownera !!
    // jedan order moze da ima vise proizvoda, ali su ti proizvodi od istog ownera
    @Transactional
    public List<OrderItem> createOrders(OrderDTO orderParam, Long userId) {

        // dodavanje u tabelu order

        Order newOrder = new Order();

        User userCustomer = userRepository.findUserById(userId).orElse(null);
        newOrder.setCustomer(userCustomer);

        User userOwner = userRepository.findByCompanyId(orderParam.getOwnerId());
        newOrder.setOwner(userOwner);

        DeliveryMethod deliveryMethod = DeliveryMethod.getById(orderParam.getDeliveryMethod());
        newOrder.setDeliveryMethod(deliveryMethod);

        // ako je delivery metoda, mora da postoji adresa
        if(deliveryMethod.equals(DeliveryMethod.DELIVERY)) {
            // ako su uneti parametri onda dodaje te parametre
            if(orderParam.getCityName() != null && orderParam.getStreetName() != null && orderParam.getStreetNumber() != null) {
                City city = cityRepository.findCityByName(orderParam.getCityName()).orElseGet(() -> {
                    City newCity = new City();
                    newCity.setName(orderParam.getCityName());
                    return cityRepository.save(newCity);
                });

                // Pronađite ili kreirajte ulicu (Street)
                Street street = streetRepository.findStreetByNameAndCity(orderParam.getStreetName(), city).orElseGet(() -> {
                    Street newStreet = new Street();
                    newStreet.setName(orderParam.getStreetName());
                    newStreet.setCity(city);
                    return streetRepository.save(newStreet);
                });

                newOrder.setDeliveryStreet(street);
                newOrder.setDeliveryStreetNumber(orderParam.getStreetNumber());
            }
            // ako nisu uneti onda uzima mesto stanovanja
            else if (orderParam.getCityName() == null && orderParam.getStreetName() == null && orderParam.getStreetNumber() == null) {
                if(userCustomer.getStreet() != null) {
                    newOrder.setDeliveryStreet(userCustomer.getStreet());
                    newOrder.setDeliveryStreetNumber(userCustomer.getStreetNumber());
                }
                else {
                    throw new IllegalArgumentException("if user chooses delivery method, he needs to add address.");
                }
            }
        }

        PaymentMethod paymentMethod = PaymentMethod.getById(orderParam.getPaymentMethod());
        newOrder.setPaymentMethod(paymentMethod);

        StatusOrder statusOrder = StatusOrder.READY_FOR_DELIVERY;
        newOrder.setStatus(statusOrder);
        newOrder.setDateOrderIsMade(new Date());

        Order savedOrder = orderRepository.save(newOrder);


        System.out.println(savedOrder);
        // dodavanje u OrderItem

        List<OrderItem> added = new ArrayList<>();

        // moraju da budu iste duzine,jer to znaci da je za svaki proizvod poslata i kolicina
        System.out.println(orderParam.getProductIds().size() + " : " + orderParam.getQuantities().size());
        if(orderParam.getProductIds().size() == orderParam.getQuantities().size()) {

            for (int i = 0; i < orderParam.getProductIds().size(); i++) {
                System.out.println("USOOO");
                Long productId = orderParam.getProductIds().get(i);
                Double quantity = orderParam.getQuantities().get(i);
                OrderItem orderItem = new OrderItem();

                Product product = productRepository.findProductById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
                orderItem.setProduct(product);

                orderItem.setOrder(savedOrder);
                orderItem.setQuantity(quantity);

                added.add(orderItemRepository.save(orderItem));
                System.out.println("KRAJ");
                // moramo i da updejtujemo quantity za taj proizvod u tabeli proizvoda
                Double newQuantity = product.getQuantity() - quantity;
                if(newQuantity >= 0) {
                    product.setQuantity(newQuantity);
                    productRepository.save(product);
                }
                else {
                    throw new IllegalArgumentException("You've ordered more than it's possible.");
                }
            }
        }
        else {
            throw new IllegalArgumentException("Check number of quantities you've sent.");
        }

        System.out.println("BBBBBBBBBBB");
        return added;
    }

    // vraca ordere od kupca (kupac)
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(Long userId, Consumer<Order> init) {
        List<Order> orders = orderRepository.findOrdersByCustomerId(userId);

        if(init != null) {
            orders.forEach(init);
        }

        return orders;
    }

    // vraca ordere od kupaca (proizvodjac)
    @Transactional(readOnly = true)
    public List<Order> getOrdersByOwnerId(Long userId) {
        List<Order> orders = orderRepository.findOrdersByOwnerId(userId);
        return orders;
    }

    // vraca ordere od kupaca (proizvodjac) - i statusom ready_for_delivery
    @Transactional(readOnly = true)
    public List<Order> getOrdersByOwnerIdAndReadyForDelivery(Long userId, StatusOrder statusOrder, Consumer<Order> init) {
        List<Order> orders = orderRepository.findOrderByOwnerIdAndStatus(userId, statusOrder);

        if(init != null) {
            orders.forEach(init);
        }

        return orders;
    }

    // uzima listu prodaja proizvodjaca
    @Transactional(readOnly = true)
    public List<Order> getMySales(Long userId) {
        List<Order> orders = orderRepository.findOrderByOwnerIdAndStatus(userId, StatusOrder.DELIVERED);

        return orders;
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId, Consumer<Order> init) {
        Optional<Order> result = orderRepository.findOrderById(orderId);

        if(init != null) {
            result.ifPresent(init);
        }

        return result.orElse(null);
    }

    // vezuje delivery za order
    @Modifying
    public Order updateDelivery(Long orderId, Long deliveryId) {
        Order order = orderRepository.findOrderById(orderId).orElse(null);
        Delivery delivery = deliveryRepository.findDeliveryById(deliveryId).orElse(null);

        order.setDelivery(delivery);

        // i menja se status
        StatusOrder statusOrder = StatusOrder.OUT_FOR_DELIVERY;
        order.setStatus(statusOrder);

        return orderRepository.save(order);
    }

    @Modifying
    public Order updateStatus(Long orderId, StatusOrder statusOrder) {
        Order order = orderRepository.findOrderById(orderId).orElse(null);

        order.setStatus(statusOrder);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByDeliveriesAndStatus(List<Delivery> deliveries, Consumer<Order> init) {
        List<Order> orders = orderRepository.findOrdersByDeliveryInAndStatus(deliveries, StatusOrder.OUT_FOR_DELIVERY);

        if(init != null) {
            orders.forEach(init);
        }

        return orders;
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByDeliveries(List<Delivery> deliveries, Consumer<Order> init) {
        List<Order> orders = orderRepository.findOrdersByDeliveryIn(deliveries);

        if(init != null) {
            orders.forEach(init);
        }

        return orders;
    }
}
