package com.ai.tool.mcp.service;

import com.ai.tool.mcp.model.DailyWeather;
import com.ai.tool.mcp.model.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach()
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "restClient", (Supplier<RestClient>) () -> restClient);
        doReturn(this.requestSpec).when(this.restClient).get();
        doReturn(this.requestSpec).when(this.requestSpec).uri(anyString(), anyMap());
        doReturn(this.responseSpec).when(this.requestSpec).retrieve();
    }

    @Test
    void should_return_weather_data_for_location() {
        // GIVEN
        double latitude = 24.237;
        double longitude = 37.928;

        WeatherResponse response = new WeatherResponse(new DailyWeather(List.of("20"), List.of("15"), List.of("10"), List.of("4")));
        doReturn(response).when(this.responseSpec).body(WeatherResponse.class);

        // WHEN
        WeatherResponse weatherData = this.weatherService.getWeatherForLocation(latitude, longitude);

        // THEN
        assertNotNull(weatherData);
        assertEquals(1, weatherData.daily().temperature_2m_max().size());
        assertEquals("20", weatherData.daily().temperature_2m_max().getFirst());
        assertEquals(1, weatherData.daily().temperature_2m_mean().size());
        assertEquals("15", weatherData.daily().temperature_2m_mean().getFirst());
        assertEquals(1, weatherData.daily().temperature_2m_min().size());
        assertEquals("10", weatherData.daily().temperature_2m_min().getFirst());
        assertEquals(1, weatherData.daily().weather_code().size());
        assertEquals("4", weatherData.daily().weather_code().getFirst());
    }

    @Test
    void should_throw_exception_when_response_null() {
        // GIVEN
        double latitude = 24.237;
        double longitude = 37.928;

        doReturn(null).when(this.responseSpec).body(WeatherResponse.class);

        // WHEN
        // THEN
        Exception exception = assertThrows(IllegalArgumentException.class, () -> this.weatherService.getWeatherForLocation(latitude, longitude));

        assertEquals("Météo introuvable : " + latitude + "//" + longitude, exception.getMessage());
    }

    @Test
    void should_throw_exception_when_results_null() {
        // GIVEN
        double latitude = 24.237;
        double longitude = 37.928;

        WeatherResponse response = new WeatherResponse(null);
        doReturn(response).when(this.responseSpec).body(WeatherResponse.class);

        // WHEN
        // THEN
        assertThrows(IllegalArgumentException.class, () -> this.weatherService.getWeatherForLocation(latitude, longitude));
    }

}