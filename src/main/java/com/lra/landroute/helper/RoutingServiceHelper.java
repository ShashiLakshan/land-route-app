package com.lra.landroute.helper;

import com.lra.landroute.algorithm.ShortestRouteFinder;
import com.lra.landroute.client.CountryDataClient;
import com.lra.landroute.exception.InvalidCountryException;
import com.lra.landroute.graph.CountryBorderGraphBuilder;
import com.lra.landroute.model.Country;
import com.lra.landroute.util.StringUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class RoutingServiceHelper {

    private final CountryDataClient countryDataClient;
    private final CountryBorderGraphBuilder countryBorderGraphBuilder;
    private final ShortestRouteFinder shortestRouteFinder;

    private volatile Map<String, List<String>> adjacencyList = Map.of();

    @PostConstruct
    void initialize() {
        List<Country> countries = countryDataClient.loadCountries();
        this.adjacencyList = countryBorderGraphBuilder.build(countries);
    }

    public void validateCountry(String countryCode, String parameterName) {
        String normalizedCode = StringUtil.normalize(countryCode);

        if (normalizedCode.isBlank() || !exists(normalizedCode)) {
            throw new InvalidCountryException(
                    "Invalid " + parameterName + " country code: " + countryCode
            );
        }
    }

    public List<String> findShortestRoute(String origin, String destination) {
        return shortestRouteFinder.findShortestRoute(origin, destination, getBorderGraph());
    }

    private boolean exists(String cca3) {
        return adjacencyList.containsKey(StringUtil.normalize(cca3));
    }

    private Map<String, List<String>> getBorderGraph() {
        return adjacencyList;
    }

}
