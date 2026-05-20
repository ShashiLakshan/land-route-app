package com.lra.landroute.client.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lra.landroute.client.CountryDataClient;
import com.lra.landroute.exception.CountryDataLoadException;
import com.lra.landroute.model.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HttpCountryDataClient implements CountryDataClient {

    @Value("${countries.data-url}")
    private String countriesDataUrl;

    private final ObjectMapper objectMapper;

    @Override
    public List<Country> loadCountries() {
        try (InputStream inputStream = URI.create(countriesDataUrl).toURL().openStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException | IllegalArgumentException ex) {
            throw new CountryDataLoadException(
                    "Unable to load country data from " + countriesDataUrl,
                    ex
            );
        }
    }
}
