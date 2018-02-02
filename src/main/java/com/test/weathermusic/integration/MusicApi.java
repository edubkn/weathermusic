package com.test.weathermusic.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.weathermusic.dto.ItemDto;
import com.test.weathermusic.util.URIBuilder;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Eduardo on 30/01/2018.
 */
@Service
@CacheConfig(cacheNames = "music")
public class MusicApi {

    public static final String MUSIC_AUTH_PATH = "/api/token";
    public static final String PATH_SEARCH_PLAYLISTS = "/v1/search";
    public static final String PATH_BROWSE_FEATURED_PLAYLISTS = "/v1/browse/featured-playlists";

    @Value("${music.api.auth.uri}")
    private String API_TOKEN;
    @Value("${music.api.uri}")
    private String HOST;
    @Value("${music.api.client.id}")
    private String CLIENT_ID;
    @Value("${music.api.secret.key}")
    private String SECRET_KEY;

    @Autowired
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String accessToken = null;

    private String getAccessToken() {
        if (accessToken == null) {
            generateNewToken();
        }
        return accessToken;
    }

    private void generateNewToken() {
        final String encodedCreds = new String(Base64.encodeBase64((CLIENT_ID + ":" + SECRET_KEY).getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCreds);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        JsonNode response = restTemplate.postForObject(API_TOKEN + MUSIC_AUTH_PATH, request, JsonNode.class);
        this.accessToken = response.findValue("access_token").asText();
    }

    @Cacheable
    public List<String> searchPlaylists(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("type", "playlist");
        paramMap.add("q", query);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                URIBuilder.buildWithParams(HOST + PATH_SEARCH_PLAYLISTS, paramMap),
                HttpMethod.GET,
                entity,
                JsonNode.class);
        String tracksUrl = response.getBody()
                .findValue("tracks")
                .findValue("href").asText();
        return getTrackNames(tracksUrl);
    }

    private List<String> getTrackNames(String tracksUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("limit", "10");

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                URIBuilder.buildWithParams(tracksUrl, paramMap),
                HttpMethod.GET,
                entity,
                JsonNode.class);
        ItemDto[] items = new ItemDto[0];
        try {
            items = objectMapper.treeToValue(response.getBody().findValue("items"), ItemDto[].class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return Stream.of(items).map(i -> i.getTrack().getName()).collect(Collectors.toList());
    }

    @Cacheable
    public List<String> getFromFeaturedPlaylists() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        final MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                URIBuilder.buildWithParams(HOST + PATH_BROWSE_FEATURED_PLAYLISTS, paramMap),
                HttpMethod.GET,
                entity,
                JsonNode.class);
        List<JsonNode> items = response.getBody().findValues("items");

        return items.stream()
             .flatMap(i -> i.findValues("tracks").stream())
             .map(t -> t.findValue("href"))
             .map(h -> getTrackNames(h.asText()).get(0))
             .limit(10)
             .collect(Collectors.toList());
    }
}
