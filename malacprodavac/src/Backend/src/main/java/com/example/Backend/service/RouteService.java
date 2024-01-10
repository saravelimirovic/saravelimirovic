package com.example.Backend.service;

import com.example.Backend.dto.DeliveryDTO;
import com.example.Backend.entity.City;
import com.example.Backend.entity.Route;
import com.example.Backend.repository.CityRepository;
import com.example.Backend.repository.RouteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RouteService {

    private final RouteRepository routeRepository;
    private final CityRepository cityRepository;


    @Transactional(readOnly = true)
    public List<Route> getRoutesByGivenRoutes(DeliveryDTO param, Consumer<Route> init) {
        List<Route> routes = routeRepository.findRoutesByStartPointCityNameLikeAndEndPointCityNameLike(param.getStartPointCityName(),
                                                                                param.getEndPointCityName());

        if(init != null) {
            routes.forEach(init);
        }

        return routes;
    }


}
