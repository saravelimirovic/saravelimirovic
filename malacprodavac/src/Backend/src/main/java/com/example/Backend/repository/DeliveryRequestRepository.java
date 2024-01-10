package com.example.Backend.repository;

import com.example.Backend.entity.DeliveryRequest;
import com.example.Backend.entity.NewRoute;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRequestRepository extends ListCrudRepository<DeliveryRequest, Long> {
    List<DeliveryRequest> findAllByDelivererId(Long delivererId);
}
