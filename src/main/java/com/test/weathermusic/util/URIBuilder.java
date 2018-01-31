package com.test.weathermusic.util;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Created by Eduardo on 31/01/2018.
 */
public class URIBuilder {

    public static URI buildWithParams(String uriString, MultiValueMap<String, String> paramMap) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(uriString)
                .queryParams(paramMap)
                .build();
        return uriComponents.toUri();
    }
}
