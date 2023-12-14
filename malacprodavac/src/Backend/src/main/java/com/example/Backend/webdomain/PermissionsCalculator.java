package com.example.Backend.webdomain;

import com.example.Backend.entity.User;
import com.example.Backend.entity.UserRoles;

import java.util.ArrayList;
import java.util.List;

public class PermissionsCalculator {
    public static List<String> calculateUserRoles(User user) {

        List<String> stringRoles = new ArrayList<>();
        user.getUserRoles().forEach(role -> {
            if (UserRoles.CUSTOMER.equals(role)) {
                stringRoles.add(WebRoles.ROLE_CUSTOMER);
            }
            else if (UserRoles.PRODUCER.equals(role)) {
                stringRoles.add(WebRoles.ROLE_PRODUCER);
            }
            else if (UserRoles.DELIVERER.equals(role)) {
                stringRoles.add(WebRoles.ROLE_DELIVERER);
            }
        });

        return stringRoles;
    }
}
