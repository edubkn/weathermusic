package com.test.weathermusic.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandKey;
import com.test.weathermusic.dto.PlaylistParamsDto;
import com.test.weathermusic.util.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by Eduardo on 31/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CircuitBreakerTests {

    private static final PlaylistParamsDto P_CITY = new PlaylistParamsDto("Araraquara");

    @Value("${weather.api.uri}")
    private String WEATHER_URI;

    @MockBean
    private RestTemplate restTemplate;
    
    @Autowired
    private WeatherApi weatherApi;

    @Before
    public void setup() {
        weatherApi = Mockito.spy(this.weatherApi);
    }

    @Test
    public void circuitBreakerClosedOnSuccess() throws IOException, InterruptedException {
        when(restTemplate.getForObject(
                URIBuilder.buildWithParams(WEATHER_URI, weatherApi.buildMapFromParams(P_CITY)),
                JsonNode.class)
        ).thenReturn(new TextNode("27.5"));

        weatherApi.getTemperature(P_CITY);
        HystrixCircuitBreaker circuitBreaker = getCircuitBreaker();
        assertThat(circuitBreaker.allowRequest(), is(true));

        verify(restTemplate, times(1)).getForObject(any(URI.class), any());
    }

//    @Test
//    public void circuitBreakerOpenOnException() throws IOException, InterruptedException {
//
//        when(restTemplate.getForObject(
//                URIBuilder.buildWithParams(WEATHER_URI, weatherApi.buildMapFromParams(P_CITY)),
//                JsonNode.class)
//        ).thenThrow(new RuntimeException());
//
//        try {
//            weatherApi.getTemperature(P_CITY);
//        } catch (RuntimeException exception) {
//            HystrixCircuitBreaker circuitBreaker = getCircuitBreaker();
//            assertThat(circuitBreaker.allowRequest(), is(false));
//        }
//
//        verify(weatherApi, times(1)).tempFromCache(any(PlaylistParamsDto.class));
//    }

    public static HystrixCircuitBreaker getCircuitBreaker() {
        return HystrixCircuitBreaker.Factory.getInstance(getCommandKey());
    }

    private static HystrixCommandKey getCommandKey() {
        return HystrixCommandKey.Factory.asKey("getTemperature");
    }

}
