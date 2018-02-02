package com.test.weathermusic.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class PlaylistIntegrationTests {

	private TestRestTemplate restTemplate = new TestRestTemplate();
	private Locale loc = new Locale("pt", "BR");

	@Value("${app.host}")
	private String host;

	@LocalServerPort
	private String port;

	@Autowired
	private ResourceBundleMessageSource messageSource;

	@Test
	public void testPlaylistCity() {
		ResponseEntity<String> response = restTemplate.getForEntity(buildUri() + "/playlist?city=Araraquara", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody(), notNullValue());
	}

	@Test
	public void testPlaylistCoords() {
		ResponseEntity<String> response = restTemplate.getForEntity(buildUri() + "/playlist?lat=-48.18&lon=-21.79", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.OK));
		assertThat(response.getBody(), notNullValue());
	}

	@Test
	public void testPlaylistCoordsNoLat() {
		ResponseEntity<String> response = restTemplate.getForEntity(buildUri() + "/playlist?lon=-21.79", String.class);
		assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
		assertThat(response.getBody(), containsString(getMessage("validation.playlist.params")));
	}

	private String buildUri() {
		return "http://" + host + ":" + port;
	}

	private String getMessage(String key) {
		return messageSource.getMessage(key, null, loc);
	}
}
