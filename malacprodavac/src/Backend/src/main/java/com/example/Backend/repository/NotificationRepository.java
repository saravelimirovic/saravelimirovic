package com.example.Backend.repository;

import com.example.Backend.entity.FirebaseNotification;
import com.example.Backend.entity.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends ListCrudRepository<Notification, Long> {
    List<Notification> findAllByReceiverEmail(String email);
}
