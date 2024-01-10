package com.example.Backend.repository;

import com.example.Backend.entity.City;
import com.example.Backend.entity.Route;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface RouteRepository extends ListCrudRepository<Route, Long>  {

    List<Route> findRoutesByStartPointCityNameLikeAndEndPointCityNameLike(String startPointCityName, String endPointCityName);
}
