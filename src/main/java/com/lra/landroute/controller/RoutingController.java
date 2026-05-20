package com.lra.landroute.controller;

import com.lra.landroute.dto.RouteResponseDTO;
import com.lra.landroute.service.RoutingService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/routing")
public class RoutingController {

    private final RoutingService routingService;

    @GetMapping(
            value = "/{origin}/{destination}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RouteResponseDTO> route(
            @PathVariable
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Origin must be a 3-letter country code")
            String origin,

            @PathVariable
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "Destination must be a 3-letter country code")
            String destination
    ) {

        RouteResponseDTO response = routingService.findRoute(origin, destination);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
