package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.entity.*;
import com.example.Backend.fileSystemImpl.FileSystemUtil;
import com.example.Backend.fileSystemImpl.enums.ImageType;
import com.example.Backend.repository.DeliveryRepository;
import com.example.Backend.repository.DeliveryRequestRepository;
import com.example.Backend.repository.NewRouteRepository;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.service.CompanyService;
import com.example.Backend.service.FirebaseMessagingService;
import com.example.Backend.service.NewRouteService;
import com.example.Backend.service.UserService;
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

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/web/route")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NewRouteController {
    private final NewRouteRepository routeRepository;
    private final DeliveryRequestRepository deliveryRequestRepository;
    private final UserRepository userRepository;
    private final FirebaseMessagingService firebaseMessagingService;
    private final CompanyService companyService;
    private final FileSystemUtil fileSystem;

    @PostMapping("/add")
    public ResponseEntity<?> addOrder(@RequestBody NewRouteDTO route) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena
            NewRoute newRoute = new NewRoute();
            newRoute.setDelivererId(userId);
            newRoute.setTime(route.getTime());
            newRoute.setDate(route.getDate());
            newRoute.setCityStart(route.getCityStart());
            newRoute.setCityEnd(route.getCityEnd());
            routeRepository.save(newRoute);

            return ResponseEntity.ok(
                    Map.of("Message", true)
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/addRequest")
    public ResponseEntity<?> addRequest(@RequestBody DeliveryRequestDTO requestDTO) {
        try {
            Long userId = getUserDetails().getId(); // prodavac
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setDelivererId(requestDTO.getDelivererId());
            deliveryRequest.setSellerId(userId);
            deliveryRequest.setCityStart(requestDTO.getCityStart());
            deliveryRequest.setCityEnd(requestDTO.getCityEnd());
            deliveryRequest.setDate(requestDTO.getDate());
            deliveryRequest.setTime(requestDTO.getTime());
            deliveryRequestRepository.save(deliveryRequest);

            Optional<User> user = userRepository.findUserById(userId);

            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTitle("Zahtev za dostavu");
            notificationMessage.setBody(user.get().getFirstName() + " " + user.get().getLastName() + " je poslao/la zahtev za dostavu!");
            notificationMessage.setTo(requestDTO.getDelivererId());
            notificationMessage.setEmail(user.get().getEmail());
            firebaseMessagingService.sendRequestDeliveryNotification(notificationMessage);

            return ResponseEntity.ok(
                    Map.of("Message", true)
            );
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMyRoutes")
    public ResponseEntity<?> getMyRoutes() {
        try {
            Long userId = getUserDetails().getId(); // dostavljac
            List<NewRoute> routes = routeRepository.findAllByDelivererId(userId);
            List<NewRouteDTO> list = new ArrayList<>();
            for (NewRoute newRoute : routes) {
                NewRouteDTO dto = new NewRouteDTO();
                dto.setCityStart(newRoute.getCityStart());
                dto.setCityEnd(newRoute.getCityEnd());
                dto.setDate(newRoute.getDate());
                dto.setTime(newRoute.getTime());
                list.add(dto);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllDeliveryRequests/{deliveryCity}")
    public ResponseEntity<?> getRequests(@PathVariable String deliveryCity) {
        try {
            Long userId = getUserDetails().getId(); // kupljenje iz tokena

            List<Company> companies = companyService.getCompanyByUserId(userId, u -> {
                Hibernate.initialize(u.getStreet());
                Hibernate.initialize(u.getStreet().getCity());
            });
            Company myCompany = companies.get(0);
            City city = myCompany.getStreet().getCity();
            List<NewRoute> routes = routeRepository.findNewRouteByCityStartAndCityEnd(city.getName(), deliveryCity);
            List<AvailableDeliveryDTO> availableDelivery = new ArrayList<>();
            for (NewRoute oneRoute : routes) {
                AvailableDeliveryDTO one = new AvailableDeliveryDTO();
                one.setUserId(oneRoute.getDelivererId());
                one.setTo(oneRoute.getCityEnd());
                one.setFrom(oneRoute.getCityStart());
                one.setDate(oneRoute.getDate());
                one.setTime(oneRoute.getTime());
                Optional<User> user = userRepository.findUserById(oneRoute.getDelivererId());
                one.setFirstNameUser(user.get().getFirstName());
                one.setLastNameUser(user.get().getLastName());
                one.setUserImage(fileSystem.getImageInBytes(String.valueOf(oneRoute.getDelivererId()), ImageType.USER));
                availableDelivery.add(one);
            }

            return new ResponseEntity<>(availableDelivery, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllRequests")
    public ResponseEntity<?> getAllRequests() {
        try {
            Long userId = getUserDetails().getId(); // dostavljac
            List<DeliveryRequestListDTO> list = new ArrayList<>();
            List<DeliveryRequest> deliveryRequests = deliveryRequestRepository.findAllByDelivererId(userId);
            for (DeliveryRequest request : deliveryRequests) {
                DeliveryRequestListDTO dto = new DeliveryRequestListDTO();
                dto.setId(request.getId());
                dto.setDate(request.getDate());
                dto.setTime(request.getTime());
                dto.setCityStart(request.getCityStart());
                dto.setCityEnd(request.getCityEnd());

                List<Company> companies = companyService.getCompanyByUserId(request.getSellerId(), u -> {
                    Hibernate.initialize(u.getStreet());
                    Hibernate.initialize(u.getStreet().getCity());
                });
                Company myCompany = companies.get(0);
                Optional<User> user = userRepository.findUserById(request.getSellerId());

                dto.setCompanyName(myCompany.getName());
                dto.setFirstNameUser(user.get().getFirstName());
                dto.setLastNameUser(user.get().getLastName());
                dto.setImage(fileSystem.getImageInBytes(String.valueOf(myCompany.getId()), ImageType.LOGO));
                list.add(dto);
            }

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/deleteDeliveryRequest/{id}/{message}")
    public ResponseEntity<?> deleteDeliveryRequestById(@PathVariable Long id, @PathVariable String message) {
        try {
            Long userId = getUserDetails().getId(); // dostavljac
            Optional<DeliveryRequest> request = deliveryRequestRepository.findById(id);
            Long sellerId = request.get().getSellerId();
            deliveryRequestRepository.deleteById(id);

            if (message.equals("Prihvaceno")) {
                Optional<User> user = userRepository.findUserById(userId);
                System.out.println("Prihvacneo");
                NotificationMessage notificationMessage = new NotificationMessage();
                notificationMessage.setTitle("Odgovor na zahtev za dostavu");
                notificationMessage.setBody(user.get().getFirstName() + " " + user.get().getLastName() + " je prihvatio/la zahtev za dostavu!");
                notificationMessage.setTo(sellerId);
                notificationMessage.setEmail(user.get().getEmail());
                firebaseMessagingService.sendResponseDeliveryNotification(notificationMessage);
            }
            else {
                Optional<User> user = userRepository.findUserById(userId);

                NotificationMessage notificationMessage = new NotificationMessage();
                notificationMessage.setTitle("Odgovor na zahtev za dostavu");
                notificationMessage.setBody(user.get().getFirstName() + " " + user.get().getLastName() + " je odbio/la zahtev za dostavu!");
                notificationMessage.setTo(sellerId);
                notificationMessage.setEmail(user.get().getEmail());
                firebaseMessagingService.sendResponseDeliveryNotification(notificationMessage);
            }

            return ResponseEntity.ok(
                    Map.of("Message", true)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
