package com.example.Backend.controller;

import com.example.Backend.dto.NotificationMessage;
import com.example.Backend.dto.SaveTokenFirabaseNotificationDTO;
import com.example.Backend.entity.Notification;
import com.example.Backend.service.FirebaseMessagingService;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    FirebaseMessagingService firebaseMessagingService;

    @PostMapping
    public String sendNotificationByToken(@RequestBody NotificationMessage notificationMessage) throws FirebaseMessagingException {
        return firebaseMessagingService.sendNotificationByToken(notificationMessage);
    }

    @PostMapping("/add-product")
    public String addProductNotification(@RequestBody NotificationMessage notificationMessage) throws FirebaseMessagingException {
        return firebaseMessagingService.addProductNotification(notificationMessage);
    }

    @PostMapping("/save-token")
    public ResponseEntity<?> token(@RequestBody SaveTokenFirabaseNotificationDTO body) {
        firebaseMessagingService.saveUserToken(body);
        return new ResponseEntity<>("User saved", HttpStatus.OK);
    }

    @GetMapping("/my-notifications/{email}")
    public ResponseEntity<?> myNotifications(@PathVariable String email) {
        List<Notification> myNotificationsList = firebaseMessagingService.getMyNotifications(email);
        return new ResponseEntity<>(myNotificationsList, HttpStatus.OK);
    }


}
