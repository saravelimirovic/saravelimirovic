package com.example.Backend.utils;

import com.example.Backend.security.CustomUserDetails;
import org.apache.commons.lang3.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;

import java.util.*;

import static java.lang.String.valueOf;
import static java.util.Optional.*;

public class ContextUtils {

    public static CustomUserDetails getUserDetails() {
        return getNullableUserDetails()
                .orElseThrow(() -> new IllegalArgumentException("No user logged in."));
    }

    public static Optional<CustomUserDetails> getNullableUserDetails() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        return auth == null
                ? empty()
                : auth.getDetails() instanceof CustomUserDetails details
                ? of(details)
                : empty();
    }

    public static String getSessionIdentifier() {
        return getNullableUserDetails()
                .map(userDetails -> valueOf(userDetails.getSessionIdentifier()))
                .orElse(StringUtils.EMPTY);
    }
}
