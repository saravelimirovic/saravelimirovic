package com.example.Backend.repository;

import com.example.Backend.entity.NewRoute;
import com.example.Backend.entity.Order;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewRouteRepository  extends ListCrudRepository<NewRoute, Long> {
    List<NewRoute> findNewRouteByCityStartAndCityEnd(String cityStart, String cityEnd);
    List<NewRoute> findAllByDelivererId(Long delivererId);
}
