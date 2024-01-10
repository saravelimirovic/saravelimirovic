package com.example.Backend.repository;

import com.example.Backend.entity.Delivery;
import com.example.Backend.entity.Route;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends ListCrudRepository<Delivery, Long> {

    List<Delivery> findDeliveriesByRouteInAndDeliveryDateAfter(List<Route> routes, Date date);

    Long deleteDeliveriesByDeliveryDateBefore(Date date);

    Optional<Delivery> findDeliveryById(Long id);

    List<Delivery> findDeliveriesByDelivererIdAndDeliveryDateAfter(Long delivererId, Date date);

    List<Delivery> findDeliveriesByIdNotInAndDeliveryDateAfter(List<Long> ids, Date date);
}
