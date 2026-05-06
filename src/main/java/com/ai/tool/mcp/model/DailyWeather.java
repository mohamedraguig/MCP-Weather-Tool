package com.ai.tool.mcp.model;

import java.util.List;

public record DailyWeather(List<String> temperature_2m_max,
                           List<String> temperature_2m_mean,
                           List<String> temperature_2m_min) {}
