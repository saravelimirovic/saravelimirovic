package com.example.Backend.service;

import com.example.Backend.dto.DeliveryDTO;
import com.example.Backend.entity.*;
import com.example.Backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final RouteRepository routeRepository;


    // dostavljac kreira delivery
    @Transactional
    public Delivery createDelivery(DeliveryDTO param, Long userId) {
        User user = userRepository.findUserById(userId).orElse(null);

        if(user == null)
            return null;

        // dodavanje rute

        Route newRoute = new Route();

        City startPointCity = cityRepository.findCityByName(param.getStartPointCityName()).orElseGet(() -> {
            City newCity = new City();
            newCity.setName(param.getStartPointCityName());
            return cityRepository.save(newCity);
        });

        newRoute.setStartPointCity(startPointCity);
        newRoute.setStartPointCityPostalCode(param.getStartPointCityPostalCode());

        City endPointCity = cityRepository.findCityByName(param.getEndPointCityName()).orElseGet(() -> {
            City newCity = new City();
            newCity.setName(param.getEndPointCityName());
            return cityRepository.save(newCity);
        });

        newRoute.setEndPointCity(endPointCity);
        newRoute.setEndPointCityPostalCode(param.getEndPointCityPostalCode());

        Route addedRoute = routeRepository.save(newRoute);



        // dodavanje deliverija

        Delivery newDelivery = new Delivery();
        newDelivery.setDeliveryDate(param.getDateOfDeparture());
        newDelivery.setDeliverer(user);
        newDelivery.setRoute(addedRoute);

        return deliveryRepository.save(newDelivery);
    }


    @Transactional
    public List<Delivery> getDeliveriesByRoutes(List<Route> routes, Consumer<Delivery> init) {
//        // brise rute kojim je prosao rok
//        Long deleteDeliveries = deliveryRepository.deleteDeliveriesByDeliveryDateBefore(new Date());
//        System.out.println(deleteDeliveries);

        List<Delivery> deliveries = deliveryRepository.findDeliveriesByRouteInAndDeliveryDateAfter(routes, new Date());

        if(init != null) {
            deliveries.forEach(init);
        }

        return deliveries;
    }

    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesByUserId(Long userId, Consumer<Delivery> init) {
        List<Delivery> deliveries = deliveryRepository.findDeliveriesByDelivererIdAndDeliveryDateAfter(userId, new Date());

        if(init != null) {
            deliveries.forEach(init);
        }

        return deliveries;
    }

    @Transactional(readOnly = true)
    public List<Delivery> getDeliveriesNotIn(List<Long> deliveries, Consumer<Delivery> init) {
        List<Delivery> result = deliveryRepository.findDeliveriesByIdNotInAndDeliveryDateAfter(deliveries, new Date());

        if(init != null) {
            result.forEach(init);
        }

        return result;
    }
}
