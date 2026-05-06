package com.ai.tool.mcp.service;

import com.ai.tool.mcp.model.GeocodingEntry;
import com.ai.tool.mcp.model.GeocodingResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeocodingService {

    private static final String GEOCODING_API = "https://geocoding-api.open-meteo.com/v1/search?format=json&count=1&language=fr&name={city}";

    private final RestClient restClient = RestClient.create();

    public GeocodingEntry getCityGeoCoding(String city) {
        GeocodingResult response =  this.restClient.get()
                .uri(GEOCODING_API, city)
                .retrieve()
                .body(GeocodingResult.class);

        if (response == null || response.results() == null || response.results().isEmpty()) {
            throw new IllegalArgumentException("Ville introuvable :" + city);
        }

        return response.results().getFirst();
    }
}
