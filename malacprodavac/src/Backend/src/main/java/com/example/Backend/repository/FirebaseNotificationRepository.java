package com.example.Backend.repository;

import com.example.Backend.entity.FirebaseNotification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FirebaseNotificationRepository extends ListCrudRepository<FirebaseNotification, Long> {
    FirebaseNotification findByEmail(String email);
    @Query("SELECT n.token FROM FirebaseNotification n")
    List<String> findUserTokens();

    List<FirebaseNotification> findAll();
}
