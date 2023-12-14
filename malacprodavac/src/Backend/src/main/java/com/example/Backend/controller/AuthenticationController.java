package com.example.Backend.controller;

import com.example.Backend.dto.*;
import com.example.Backend.entity.User;
import com.example.Backend.security.*;
import com.example.Backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.*;
import java.util.*;
import java.util.stream.*;

// ovde stavljamo stvari za koristika za koje se zahteva da nema token
@RestController
@RequestMapping("/web")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationController {

    private final UserService userService;

    @GetMapping("/me")
    public AuthenticationW getMyUser(Principal principal) {

        AuthenticationW.AuthenticationWBuilder builder = AuthenticationW.builder();

        boolean authenticated = false;

        if (principal instanceof UsernamePasswordAuthenticationToken principalExtended) {

            authenticated = principalExtended.isAuthenticated();

            builder.authorities(prepareAuthorities(principalExtended))
                    .firstName(getUsersFirstName(principalExtended))
                    .lastName(getUsersLastName(principalExtended));

        }

        builder.authenticated(authenticated);

        return builder.build();
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Object> registerUser(@RequestBody RegisterParam param) {
        if(param == null) {
            return ResponseEntity.badRequest().body("No params provided");
        }

        try {
            User dodat = userService.createUser(param);

            return ResponseEntity.ok(
                    Map.of("Message", 1, "idAddedUser", dodat.getId())
            );
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        //userService.createUser(param);
    }

    private String getUsersFirstName(UsernamePasswordAuthenticationToken principalExtended) {

        CustomUserDetails userDetails = (CustomUserDetails) principalExtended.getDetails();

        return userDetails.getFirstName();
    }

    private String getUsersLastName(UsernamePasswordAuthenticationToken principalExtended) {

        CustomUserDetails userDetails = (CustomUserDetails) principalExtended.getDetails();

        return userDetails.getLastName();
    }

    private List<String> prepareAuthorities(UsernamePasswordAuthenticationToken principalExtended) {
        return principalExtended.isAuthenticated() ? principalExtended.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()) : Collections.emptyList();
    }
}
