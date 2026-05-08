package com.ai.tool.mcp.tool;

import com.ai.tool.mcp.model.DailyWeather;
import com.ai.tool.mcp.model.GeocodingEntry;
import com.ai.tool.mcp.model.WeatherRequest;
import com.ai.tool.mcp.model.WeatherResponse;
import com.ai.tool.mcp.service.GeocodingService;
import com.ai.tool.mcp.service.WeatherService;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherToolTest {
    @Mock
    private GeocodingService geocodingService;
    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherTool weatherTool;

    @Test
    void should_return_weather_data_as_structured_content() {
        // GIVEN
        String city = "Lyon";
        double latitude = 25.2763;
        double longitude = 46.625;
        GeocodingEntry entry = new GeocodingEntry(latitude, longitude);

        DailyWeather daily = new DailyWeather(List.of("20"), List.of("15"), List.of("10"), List.of("4"));
        WeatherResponse response = new WeatherResponse(daily);

        doReturn(entry).when(this.geocodingService).getCityGeoCoding(anyString());
        doReturn(response).when(this.weatherService).getWeatherForLocation(anyDouble(), anyDouble());

        // WHEN
        CallToolResult callToolResult = this.weatherTool.getWeather(new WeatherRequest(city));

        // THEN
        assertNotNull(callToolResult);
        assertFalse(callToolResult.isError());
        assertThat(callToolResult.content())
                .anyMatch(c -> c.toString().contains("Je vous ai trouvé la météo"));
        assertThat(callToolResult.structuredContent())
                .extracting("city", "lat", "lon", "date", "daily")
                .containsExactly(city, latitude, longitude, LocalDate.now().toString(), daily);
        verify(this.geocodingService, times(1)).getCityGeoCoding(city);
        verify(this.weatherService, times(1)).getWeatherForLocation(latitude, longitude);
    }

    @Test
    void should_catch_exception_and_return_error_when_geocoding_fails() {
        // GIVEN
        String city = "Lyon";

        doThrow(IllegalArgumentException.class).when(this.geocodingService).getCityGeoCoding(anyString());

        // WHEN
        CallToolResult callToolResult = this.weatherTool.getWeather(new WeatherRequest(city));

        // THEN
        assertNotNull(callToolResult);
        assertTrue(callToolResult.isError());
        assertThat(callToolResult.content())
                .anyMatch(c -> c.toString().contains("Une erreur est survenue"));
        verify(this.geocodingService, times(1)).getCityGeoCoding(city);
        verifyNoInteractions(this.weatherService);
    }

    @Test
    void should_catch_exception_and_return_error_when_weather_fails() {
        // GIVEN
        String city = "Lyon";
        double latitude = 25.2763;
        double longitude = 46.625;
        GeocodingEntry entry = new GeocodingEntry(latitude, longitude);

        DailyWeather daily = new DailyWeather(List.of("20"), List.of("15"), List.of("10"), List.of("4"));
        WeatherResponse response = new WeatherResponse(daily);

        doReturn(entry).when(this.geocodingService).getCityGeoCoding(anyString());
        doThrow(IllegalArgumentException.class).when(this.weatherService).getWeatherForLocation(anyDouble(), anyDouble());

        // WHEN
        CallToolResult callToolResult = this.weatherTool.getWeather(new WeatherRequest(city));

        // THEN
        assertNotNull(callToolResult);
        assertTrue(callToolResult.isError());
        assertThat(callToolResult.content())
                .anyMatch(c -> c.toString().contains("Une erreur est survenue"));
        verify(this.geocodingService, times(1)).getCityGeoCoding(city);
        verify(this.weatherService, times(1)).getWeatherForLocation(latitude, longitude);
    }

    @Test
    void should_return_widget_content_as_string() throws IOException {
        // GIVEN
        // WHEN
        String htmlContent = this.weatherTool.getWeatherCardUI();

        // THEN
        assertInstanceOf(String.class, htmlContent);
        assertThat(htmlContent).contains("<html");
    }

    @Test
    void should_return_ui_meta() {
        Map<String, Object> meta = new WeatherTool.UiMetaProvider().getMeta();

        assertThat(meta).containsKey("ui");
        Map<String, Object> ui = (Map<String, Object>) meta.get("ui");
        assertThat(ui).containsEntry("resourceUri", "ui://widget/weather-widget.html");
    }

    @Test
    void should_return_csp_meta() {
        Map<String, Object> meta = new WeatherTool.CspMetaProvider().getMeta();

        assertThat(meta).containsKey("openai/widgetCSP");
        Map<String, Object> widgetCsp = (Map<String, Object>) meta.get("openai/widgetCSP");
        assertThat(widgetCsp).containsEntry("connect_domains", List.of("https://geocoding-api.open-meteo.com"));
    }

}