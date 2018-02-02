# weathermusic
> Suggests songs based on weather.

## Usage

This API suggests songs based on the current weather of the location or coordinates provided by the user. Some request examples are:
GET - /playlist?city=Araraquara
GET - /playlist?lat=-21&lon=-48

This app will try to get a previously cached result of the same location if it fails to determine the current weather, or will ultimately suggest songs from the featured playlists if nothing is in the cache.

## Technical rundown

The above is achieved by using a circuit breaker (Hystrix) and a simple in-memory cache solution (Ehcache). The endpoint has a small Java Bean validation to assert the parameters are correct - either city or lat and lon are required.
The logs are enhanced by taking advantage of Spring Boot's autoconfiguration.
There are functional tests to emulate the app's behavior and a "localtest" using Wiremock to simulate a controlled environment where you don't have access to the external APIs.