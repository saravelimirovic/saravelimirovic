package com.example.Backend.service;

import com.example.Backend.dto.MyFollowingDTO;
import com.example.Backend.dto.NotificationMessage;
import com.example.Backend.dto.SaveTokenFirabaseNotificationDTO;
import com.example.Backend.entity.FirebaseNotification;
import com.example.Backend.entity.User;
import com.example.Backend.repository.FirebaseNotificationRepository;
import com.example.Backend.repository.NotificationRepository;
import com.example.Backend.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FirebaseMessagingService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;
    @Autowired
    private FirebaseNotificationRepository notificationRepository;
    @Autowired
    private FollowingService followingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationRepository notificationRepositoryToShow;

    public String sendNotificationByToken(NotificationMessage notificationMessage) throws FirebaseMessagingException {
        List<String> tokens = notificationRepository.findUserTokens();

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .build();

        for (String token : tokens) {
            Message message = Message
                    .builder()
                    .setToken(token)
                    .putData("icon", notificationMessage.getImage())
                    .setNotification(notification)
                    .build();

            firebaseMessaging.send(message);
        }
        return "Success sending notifications";
    }

    public String sendOrderNotification(NotificationMessage notificationMessage) throws FirebaseMessagingException {
        Optional<User> receiver = userRepository.findUserById(notificationMessage.getTo());
        User sender = userRepository.findUserByEmail(notificationMessage.getEmail());
        FirebaseNotification tokenReceiver = notificationRepository.findByEmail(receiver.get().getEmail());

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(tokenReceiver.getToken())
                .setNotification(notification)
                .build();

        com.example.Backend.entity.Notification pom = new com.example.Backend.entity.Notification();
        pom.setTitle(notificationMessage.getTitle());
        pom.setBody(notificationMessage.getBody());
        pom.setSenderFirstName(sender.getFirstName());
        pom.setSenderLastName(sender.getLastName());
        pom.setReceiverEmail(receiver.get().getEmail());
        pom.setDateNotificationIsSent(new Date());
        notificationRepositoryToShow.save(pom);

        firebaseMessaging.send(message);

        return "Success sending notifications";
    }

    public String sendRequestDeliveryNotification(NotificationMessage notificationMessage) throws FirebaseMessagingException {
        Optional<User> receiver = userRepository.findUserById(notificationMessage.getTo());
        User sender = userRepository.findUserByEmail(notificationMessage.getEmail());
        FirebaseNotification tokenReceiver = notificationRepository.findByEmail(receiver.get().getEmail());

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(tokenReceiver.getToken())
                .setNotification(notification)
                .build();

        com.example.Backend.entity.Notification pom = new com.example.Backend.entity.Notification();
        pom.setTitle(notificationMessage.getTitle());
        pom.setBody(notificationMessage.getBody());
        pom.setSenderFirstName(sender.getFirstName());
        pom.setSenderLastName(sender.getLastName());
        pom.setReceiverEmail(receiver.get().getEmail());
        pom.setDateNotificationIsSent(new Date());
        notificationRepositoryToShow.save(pom);

        firebaseMessaging.send(message);

        return "Success sending notifications";
    }

    public String sendResponseDeliveryNotification(NotificationMessage notificationMessage) throws FirebaseMessagingException {
        Optional<User> receiver = userRepository.findUserById(notificationMessage.getTo());
        User sender = userRepository.findUserByEmail(notificationMessage.getEmail());
        FirebaseNotification tokenReceiver = notificationRepository.findByEmail(receiver.get().getEmail());

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .build();

        Message message = Message
                .builder()
                .setToken(tokenReceiver.getToken())
                .setNotification(notification)
                .build();

        com.example.Backend.entity.Notification pom = new com.example.Backend.entity.Notification();
        pom.setTitle(notificationMessage.getTitle());
        pom.setBody(notificationMessage.getBody());
        pom.setSenderFirstName(sender.getFirstName());
        pom.setSenderLastName(sender.getLastName());
        pom.setReceiverEmail(receiver.get().getEmail());
        pom.setDateNotificationIsSent(new Date());
        notificationRepositoryToShow.save(pom);

        firebaseMessaging.send(message);

        return "Success sending notifications";
    }



    // srediti ovo, ne radi lepo
    public String addProductNotification(NotificationMessage notificationMessage) throws FirebaseMessagingException {
        User sender = userRepository.findUserByEmail(notificationMessage.getEmail()); // korisnik koji salje notifikaciju
//        System.out.println("SENDER: " + sender.getEmail());
        List<MyFollowingDTO> listOfSend = followingService.getMyFollowers(sender.getId()); // lista korisnika koje on prati
//        System.out.println("SENDER FOLLOWERS: " + listOfSend);
        List<String> listaEmailAdresa = listOfSend.stream() // samo email-ovi tih korisnika jer je to jedinstveno za korisnika
                .map(MyFollowingDTO::getEmail)
                .collect(Collectors.toList());
//        System.out.println("SENDER FOLLOWERS (EMAILS): " + listaEmailAdresa);

        List<FirebaseNotification> usersWithTokens = notificationRepository.findAll();
//        System.out.println("ALL USERS WITH TOKEN: " + usersWithTokens);
        List<String> tokens = usersWithTokens.stream()
                .filter(user -> listaEmailAdresa.contains(user.getEmail()))
                .map(FirebaseNotification::getToken)
                .collect(Collectors.toList());
//        System.out.println("USERS TO SEND(TOKENS): " + tokens);

        Notification notification = Notification
                .builder()
                .setTitle(notificationMessage.getTitle())
                .setBody(notificationMessage.getBody())
                .build();

        for (int i = 0; i < listaEmailAdresa.size(); i++) {
            com.example.Backend.entity.Notification pom = new com.example.Backend.entity.Notification();
            pom.setTitle(notificationMessage.getTitle());
            pom.setBody(notificationMessage.getBody());
            pom.setSenderFirstName(sender.getFirstName());
            pom.setSenderLastName(sender.getLastName());
            pom.setReceiverEmail(listaEmailAdresa.get(i));
            pom.setDateNotificationIsSent(new Date());
            notificationRepositoryToShow.save(pom);

            Message message = Message
                    .builder()
                    .setToken(tokens.get(i))
                    .setNotification(notification)
                    .build();

            firebaseMessaging.send(message);
        }
        return "Success sending notifications";
    }

    public void saveUserToken(SaveTokenFirabaseNotificationDTO body) {
        FirebaseNotification old = notificationRepository.findByEmail(body.getEmail());
        if (old == null) {
            FirebaseNotification notification = new FirebaseNotification();
            notification.setEmail(body.getEmail());
            notification.setToken(body.getToken());
            notificationRepository.save(notification);
        }
        else {
            old.setToken(body.getToken());
            notificationRepository.save(old);
        }

    }

    public List<com.example.Backend.entity.Notification> getMyNotifications(String email) {
        return notificationRepositoryToShow.findAllByReceiverEmail(email);
    }
}
