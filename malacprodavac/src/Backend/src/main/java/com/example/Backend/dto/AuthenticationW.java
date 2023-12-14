package com.example.Backend.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.util.*;

// vraca pri loginu
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationW {

    private final Long id;
    private final String firstName;
    private final String lastName;
    private final List<String> authorities;
    private final boolean authenticated;
    private final String token;

}
