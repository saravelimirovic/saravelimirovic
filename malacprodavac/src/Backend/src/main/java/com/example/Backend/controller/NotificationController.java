package com.example.Backend.controller;

import com.example.Backend.dto.NotificationMessage;
import com.example.Backend.dto.SaveTokenFirabaseNotificationDTO;
import com.example.Backend.entity.*;
import com.example.Backend.repository.NotificationRepository;
import com.example.Backend.repository.OrderRepository;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.service.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpOr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.example.Backend.utils.ContextUtils.getUserDetails;

@RestController
@RequestMapping("/notification")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NotificationController {

    private final FirebaseMessagingService firebaseMessagingService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    @PostMapping
    public String sendNotificationByToken(@RequestBody NotificationMessage notificationMessage) throws FirebaseMessagingException {
//        Long userId = getUserDetails().getId(); // prodavac
        return firebaseMessagingService.sendNotificationByToken(notificationMessage);
    }

    @PostMapping("/add-product")
    public String addProductNotification(@RequestBody NotificationMessage notificationMessage) throws FirebaseMessagingException {
//        Long userId = getUserDetails().getId(); // prodavac
        return firebaseMessagingService.addProductNotification(notificationMessage);
    }

    @PostMapping("/send-order")
    public String sendOrderNotification(@RequestBody NotificationMessage notificationMessage) throws FirebaseMessagingException {
//        Long userId = getUserDetails().getId(); // prodavac
        return firebaseMessagingService.sendOrderNotification(notificationMessage);
    }

    @PostMapping("/save-token")
    public ResponseEntity<?> token(@RequestBody SaveTokenFirabaseNotificationDTO body) {
//        Long userId = getUserDetails().getId(); // prodavac
        firebaseMessagingService.saveUserToken(body);
        return new ResponseEntity<>("User saved", HttpStatus.OK);
    }

    @GetMapping("/send-order-answer/{orderId}/{answer}")
    public ResponseEntity<?> sendOrderAnswer(@PathVariable Long orderId, @PathVariable String answer) throws FirebaseMessagingException {
        System.out.println(orderId);
        Optional<Order> order = orderRepository.findOrderById(orderId);
        Optional<User> owner = userRepository.findUserById(order.get().getOwner().getId()); // ja
        Company company =  owner.get().getCompanies().get(0);

        orderRepository.delete(order.get());
        if (answer.equals("Prihvaceno")) {
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTitle("Odgovor na porudžbinu");
            notificationMessage.setBody(company.getName() + " je prihvatila vašu porudžbinu");
            notificationMessage.setTo(order.get().getCustomer().getId());
            notificationMessage.setEmail(owner.get().getEmail());
            firebaseMessagingService.sendResponseDeliveryNotification(notificationMessage);
        }
        else {
            NotificationMessage notificationMessage = new NotificationMessage();
            notificationMessage.setTitle("Odgovor na porudžbinu");
            notificationMessage.setBody(company.getName() + " je odbila vašu porudžbinu");
            notificationMessage.setTo(order.get().getCustomer().getId());
            notificationMessage.setEmail(owner.get().getEmail());
            firebaseMessagingService.sendResponseDeliveryNotification(notificationMessage);
        }

        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }

    @GetMapping("/my-notifications/{email}")
    public ResponseEntity<?> myNotifications(@PathVariable String email) {
        List<Notification> myNotificationsList = firebaseMessagingService.getMyNotifications(email);
        return new ResponseEntity<>(myNotificationsList, HttpStatus.OK);
    }

    @DeleteMapping("/deleteNotification/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        try {
            notificationRepository.deleteById(id);

            return ResponseEntity.ok(
                    Map.of("Message", true)
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


}
