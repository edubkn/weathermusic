package com.test.weathermusic.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.test.weathermusic.util.URIBuilder;
import com.test.weathermusic.util.spring.CacheHelper;
import com.test.weathermusic.dto.PlaylistParamsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Created by Eduardo on 29/01/2018.
 */
@Service
public class WeatherApi {

    private static final String WEATHER_URI = "https://api.openweathermap.org/data/2.5/weather";
    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherApi.class);

    // Hardcoded parameters
    @Value("${weather.api.param.key}")
    private String P_API_KEY;
    @Value("${weather.api.key}")
    private String API_KEY;

    @Value("${weather.api.param.units}")
    private String P_UNITS;
    @Value("${weather.api.units}")
    private String UNITS;

    // User input parameters
    @Value("${weather.api.param.city}")
    private  String P_CITY;
    @Value("${weather.api.param.lat}")
    private String P_LAT;
    @Value("${weather.api.param.lon}")
    private String P_LON;

    private RestTemplate restTemplate;
    private CacheHelper cacheHelper;

    public WeatherApi(RestTemplate restTemplate, CacheHelper cacheHelper) {
        this.restTemplate = restTemplate;
        this.cacheHelper = cacheHelper;
    }

    @HystrixCommand(fallbackMethod = "tempFromCache")
    @CachePut("weather")
    public String getTemperature(PlaylistParamsDto params) {
        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add(P_API_KEY, API_KEY);
        paramMap.add(P_UNITS, UNITS);
        if (params.getCity() != null) {
            paramMap.add(P_CITY, params.getCity());
        } else {
            paramMap.add(P_LAT, String.format("%11.8f", params.getLat()));
            paramMap.add(P_LON, String.format("%11.8f", params.getLon()));
        }
        JsonNode response = restTemplate.getForObject(
                URIBuilder.buildWithParams(WEATHER_URI, paramMap),
                JsonNode.class);
        return response.findValue("temp").asText();
    }

    private String tempFromCache(PlaylistParamsDto params) {
        LOGGER.warn("Weather API call failed for {}, trying the cache", params);
        return cacheHelper.get("weather", params, String.class);
    }

}
