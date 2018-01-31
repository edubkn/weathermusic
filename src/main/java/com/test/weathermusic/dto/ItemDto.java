package com.test.weathermusic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Eduardo on 30/01/2018.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDto {

    private TrackDto track;

    public TrackDto getTrack() {
        return track;
    }

    public void setTrack(TrackDto track) {
        this.track = track;
    }
}
