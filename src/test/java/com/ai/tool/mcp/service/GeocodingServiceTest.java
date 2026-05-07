package com.ai.tool.mcp.service;

import com.ai.tool.mcp.model.GeocodingEntry;
import com.ai.tool.mcp.model.GeocodingResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec<?> requestSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(geocodingService, "restClient", (Supplier<RestClient>) () -> restClient);
        doReturn(this.requestSpec).when(this.restClient).get();
        doReturn(this.requestSpec).when(this.requestSpec).uri(anyString(), anyString());
        doReturn(this.responseSpec).when(this.requestSpec).retrieve();
    }

    @Test
    void should_retrieve_lat_lon_from_city_name() {
        // GIVEN
        GeocodingEntry entry = new GeocodingEntry(47.3873, 26.2726);
        GeocodingResult result = new GeocodingResult(List.of(entry));

        doReturn(result).when(this.responseSpec).body(GeocodingResult.class);

        // WHEN
        GeocodingEntry cityGeocoding = this.geocodingService.getCityGeoCoding("Lyon");

        // THEN
        assertNotNull(cityGeocoding);
        assertEquals(entry.latitude(), cityGeocoding.latitude());
        assertEquals(entry.longitude(), cityGeocoding.longitude());
    }

    @Test
    void should_throw_exception_on_null_response() {
        // GIVEN
        doReturn(null).when(this.responseSpec).body(GeocodingResult.class);

        // WHEN
        // THEN
        Exception thrownException = assertThrows(IllegalArgumentException.class, () -> this.geocodingService.getCityGeoCoding("Grenoble"));
        assertEquals("Ville introuvable : Grenoble", thrownException.getMessage());
    }

    @Test
    void should_throw_exception_on_null_results() {
        // GIVEN
        GeocodingResult result = new GeocodingResult(null);

        doReturn(result).when(this.responseSpec).body(GeocodingResult.class);

        // WHEN
        // THEN
        assertThrows(IllegalArgumentException.class, () -> this.geocodingService.getCityGeoCoding("Taza"));
    }

    @Test
    void should_throw_exception_on_empty_results() {
        // GIVEN
        GeocodingResult result = new GeocodingResult(Collections.emptyList());

        doReturn(result).when(this.responseSpec).body(GeocodingResult.class);

        // WHEN
        // THEN
        assertThrows(IllegalArgumentException.class, () -> this.geocodingService.getCityGeoCoding("Fes"));
    }

}