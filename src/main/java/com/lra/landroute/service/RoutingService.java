package com.lra.landroute.service;

import com.lra.landroute.dto.RouteResponseDTO;

public interface RoutingService {

    RouteResponseDTO findRoute(String origin, String destination);

}
