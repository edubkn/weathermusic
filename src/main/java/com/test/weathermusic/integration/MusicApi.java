package com.test.weathermusic.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.weathermusic.dto.ItemDto;
import com.test.weathermusic.util.URIBuilder;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Eduardo on 30/01/2018.
 */
@Service
public class MusicApi {

    private static final String API_TOKEN = "https://accounts.spotify.com/api/token";
    private static final String HOST = "https://api.spotify.com";
    private static final String PATH_SEARCH_PLAYLISTS = "/v1/search";

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
        final String creds = "4459b3d67fbd4dcb9ba3ea9a13b7b63c" + ":" + "b398d0654b724c96bd2cf683dbc15738";
        final String encodedCreds = new String(Base64.encodeBase64(creds.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCreds);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        JsonNode response = restTemplate.postForObject(API_TOKEN, request, JsonNode.class);
        this.accessToken = response.findValue("access_token").asText();
    }

    @Cacheable("music")
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




}
