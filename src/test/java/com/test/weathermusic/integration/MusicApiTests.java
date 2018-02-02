package com.test.weathermusic.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;

/**
 * Created by Eduardo on 30/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MusicApiTests {

    @Autowired
    private MusicApi musicApi;

    @Test
    public void testSearchPlaylists() throws JsonProcessingException {
        List<String> trackNames = musicApi.searchPlaylists("rock");
        assertThat(trackNames.size(), greaterThan(0));
    }

    @Test
    public void testGetFromFeaturedPlaylists() throws JsonProcessingException {
        List<String> trackNames = musicApi.getFromFeaturedPlaylists();
        assertThat(trackNames.size(), greaterThan(0));
    }
}
