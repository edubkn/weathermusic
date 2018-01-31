package com.test.weathermusic.controller;

import com.test.weathermusic.dto.PlaylistParamsDto;
import com.test.weathermusic.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by Eduardo on 29/01/2018.
 */
@RestController
public class PlaylistController {

    public static final String PATH = "/playlist";

    @Autowired
    private PlaylistService playlistService;

    @GetMapping(PATH)
    public List<String> buildPlaylist(@Valid PlaylistParamsDto params) {
        List<String> tracks = playlistService.suggestPlaylist(params);
        return tracks;
    }
}
