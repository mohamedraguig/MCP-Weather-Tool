package com.ai.tool.mcp.tool;

import com.ai.tool.mcp.model.GeocodingEntry;
import com.ai.tool.mcp.model.WeatherRequest;
import com.ai.tool.mcp.model.WeatherResponse;
import com.ai.tool.mcp.service.GeocodingService;
import com.ai.tool.mcp.service.WeatherService;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.annotation.McpResource;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.MetaProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherTool {
    private final GeocodingService geocodingService;
    private final WeatherService weatherService;

    @McpTool(
            name = "get_weather",
            description = "Retourne la météo actuelle pour une ville donnée",
            metaProvider = UiMetaProvider.class
    )
    public CallToolResult getWeather(WeatherRequest request) {
        try {
            // 1 retrieve city latitude and longitude
            GeocodingEntry geocodingEntry = this.geocodingService.getCityGeoCoding(request.city());

            // 2 retrieve daily weather (temperature) for location
            WeatherResponse weatherResponse = this.weatherService.getWeatherForLocation(geocodingEntry.latitude(), geocodingEntry.longitude());

            return CallToolResult.builder()
                    .structuredContent(Map.of(
                            "city",  request.city(),
                            "date",  LocalDate.now().toString(),
                            "lat",   geocodingEntry.latitude(),
                            "lon",   geocodingEntry.longitude(),
                            "daily", weatherResponse.daily()
                    ))
                    .addTextContent("Je vous ai trouvé la météo")
                    .isError(false)
                    .build();
        } catch (IllegalArgumentException e) {

            log.error("Une erreur est survenue lors de la récupération de la météo : {}", e.getMessage());

            return CallToolResult.builder()
                    .isError(true)
                    .addTextContent("Une erreur est survenue")
                    .build();
        }
    }

    @McpResource(
            uri = "ui://widget/weather-widget.html",
            name = "Weather Card UI",
            description = "Donne une UI representative aux résultats de météo",
            mimeType = "text/html;profile=mcp-app",
            metaProvider = CspMetaProvider.class
    )
    public String getWeatherCardUI() throws IOException {
        return new ClassPathResource("static/weather-widget.html").getContentAsString(StandardCharsets.UTF_8);
    }

    public static final class UiMetaProvider implements MetaProvider {

        @Override
        public Map<String, Object> getMeta() {
            return Map.of("ui",
                    Map.of("resourceUri", "ui://widget/weather-widget.html")
            );
        }
    }

    public static final class CspMetaProvider implements MetaProvider {

        @Override
        public Map<String, Object> getMeta() {
            return Map.of(
                    "openai/widgetCSP", Map.of(
                            "connect_domains", List.of("https://geocoding-api.open-meteo.com")
                    )
            );
        }
    }
}
