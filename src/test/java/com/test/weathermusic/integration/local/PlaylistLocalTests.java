package com.test.weathermusic.integration.local;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.codec.binary.Base64;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.common.Json.write;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.test.weathermusic.integration.MusicApi.MUSIC_AUTH_PATH;
import static com.test.weathermusic.integration.MusicApi.PATH_SEARCH_PLAYLISTS;
import static com.test.weathermusic.integration.WeatherApi.WEATHER_PATH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Created by Eduardo on 01/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("localtest")
public class PlaylistLocalTests {

    public static final String PLAYLIST_URL = "/v1/users/spotify/playlists/37i9dQZF1DXcF6B6QPhFDv/tracks";

    @ClassRule
    public static WireMockRule wireMock = new WireMockRule(options().port(8889).withRootDirectory("src/test/resources"));

    @Value("${app.host}")
    private String host;

    @LocalServerPort
    private String port;

    @Value("${weather.api.param.key}")
    private String P_API_KEY;
    @Value("${weather.api.key}")
    private String API_KEY;
    @Value("${weather.api.param.units}")
    private String P_UNITS;
    @Value("${weather.api.units}")
    private String UNITS;

    @Value("${music.api.auth.uri}")
    private String API_TOKEN;
    @Value("${music.api.client.id}")
    private String CLIENT_ID;
    @Value("${music.api.secret.key}")
    private String SECRET_KEY;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Before
    public void setUp() {
        wireMock.stubFor(get(urlPathEqualTo(WEATHER_PATH))
                .withQueryParam(P_API_KEY, equalTo(API_KEY))
                .withQueryParam(P_UNITS, equalTo(UNITS))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(write(weatherApiResponse())))
        );

        wireMock.stubFor(post(urlPathEqualTo(MUSIC_AUTH_PATH))
                .withHeader("Authorization", equalTo("Basic " +
                        new String(Base64.encodeBase64((CLIENT_ID + ":" + SECRET_KEY).getBytes()))))
                .withRequestBody(equalTo("grant_type=client_credentials"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\": \"BQDx49lHM5855csUNwBu6_mTp88g8ShCy1GMIMI3SZ8FOPgypCpYyFs7D0ciaI0esOdrDY7OzYUNiMUffgk\"}"))
        );

        wireMock.stubFor(get(urlPathEqualTo(PATH_SEARCH_PLAYLISTS))
                .withHeader("Authorization", equalTo("Bearer BQDx49lHM5855csUNwBu6_mTp88g8ShCy1GMIMI3SZ8FOPgypCpYyFs7D0ciaI0esOdrDY7OzYUNiMUffgk"))
                .withQueryParam("type", equalTo("playlist"))
                .withQueryParam("q", equalTo("rock"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("playlist.json"))
        );

        wireMock.stubFor(get(urlPathEqualTo(PLAYLIST_URL))
                .withHeader("Authorization", equalTo("Bearer BQDx49lHM5855csUNwBu6_mTp88g8ShCy1GMIMI3SZ8FOPgypCpYyFs7D0ciaI0esOdrDY7OzYUNiMUffgk"))
                .withQueryParam("limit", equalTo("10"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("track.json"))
        );
    }

    @Test
    public void testPlaylistCity() {
        ResponseEntity<String> response = restTemplate.getForEntity(buildUri() + "/playlist?city=Araraquara", String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), notNullValue());
    }

    private String buildUri() {
        return "http://" + host + ":" + port;
    }

    private JsonNode weatherApiResponse() {
        return new ObjectNode(JsonNodeFactory.instance, ImmutableMap.of("temp", new TextNode("13.5")));
    }

}
