package com.test.weathermusic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.weathermusic.dto.PlaylistParamsDto;
import com.test.weathermusic.integration.MusicApi;
import com.test.weathermusic.integration.WeatherApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Eduardo on 31/01/2018.
 */
@Service
public class PlaylistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaylistService.class);

    private WeatherApi weatherApi;
    private MusicApi musicApi;

    public PlaylistService(WeatherApi weatherApi, MusicApi musicApi) {
        this.weatherApi = weatherApi;
        this.musicApi = musicApi;
    }

    public List<String> suggestPlaylist(PlaylistParamsDto params) {
        BigDecimal temp = fetchWeather(params);
        if (temp.compareTo(BigDecimal.valueOf(30)) == 1) {
            return musicApi.searchPlaylists("party");
        } else if (temp.compareTo(BigDecimal.valueOf(15)) >= 0) {
            return musicApi.searchPlaylists("pop music");
        } else if (temp.compareTo(BigDecimal.valueOf(10)) >= 0) {
            return musicApi.searchPlaylists("rock");
        } else {
            return musicApi.searchPlaylists("classical music");
        }
    }

    private BigDecimal fetchWeather(PlaylistParamsDto params) {
        String temp = weatherApi.getTemperature(params);
        return temp != null ? new BigDecimal(temp) : BigDecimal.ZERO;
    }
}
