package com.test.weathermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Eduardo on 30/01/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDto {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
