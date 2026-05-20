package com.lra.landroute.client;

import com.lra.landroute.model.Country;

import java.util.List;

public interface CountryDataClient {

    List<Country> loadCountries();
}
