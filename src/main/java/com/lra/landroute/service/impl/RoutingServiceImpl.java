package com.lra.landroute.service.impl;

import com.lra.landroute.dto.RouteResponseDTO;
import com.lra.landroute.helper.RoutingServiceHelper;
import com.lra.landroute.service.RoutingService;
import com.lra.landroute.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoutingServiceImpl implements RoutingService {

    private final RoutingServiceHelper routingServiceHelper;

    @Override
    public RouteResponseDTO findRoute(String origin, String destination) {
        String originCode = StringUtil.normalize(origin);
        String destinationCode = StringUtil.normalize(destination);

        routingServiceHelper.validateCountry(originCode, "origin");
        routingServiceHelper.validateCountry(destinationCode, "destination");

        List<String> routeList = routingServiceHelper.findShortestRoute(originCode, destinationCode);
        return RouteResponseDTO.builder().route(routeList).build();
    }

}
