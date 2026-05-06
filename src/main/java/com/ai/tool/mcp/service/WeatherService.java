package com.ai.tool.mcp.service;

import com.ai.tool.mcp.model.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Map;

@Service
public class WeatherService {

    private static final String WEATHER_API = "https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=temperature_2m_max,temperature_2m_min,temperature_2m_mean&end_date={date}&start_date={date}";

    private final RestClient restClient = RestClient.create();

    public WeatherResponse getWeatherForLocation(double latitude, double longitude) {
        WeatherResponse response = this.restClient.get()
                .uri(WEATHER_API, Map.of("latitude", latitude, "longitude", longitude, "date", LocalDate.now().toString()))
                .retrieve()
                .body(WeatherResponse.class);

        if (response == null || response.daily() == null) {
            throw new IllegalArgumentException("Météo introuvable :" + latitude + "//" + longitude);
        }

        return response;
    }
}
