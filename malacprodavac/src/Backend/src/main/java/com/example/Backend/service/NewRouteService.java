package com.example.Backend.service;

import com.example.Backend.dto.NewRouteDTO;
import com.example.Backend.repository.NewRouteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NewRouteService {
    private final NewRouteRepository routeRepository;

}
