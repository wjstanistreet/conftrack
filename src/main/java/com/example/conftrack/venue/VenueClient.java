package com.example.conftrack.venue;

import com.example.conftrack.config.VenueProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Calls Facilities' Venue API. The contract is specs/venue-api.yaml. */
@Component
public class VenueClient {

    private static final Logger log = LoggerFactory.getLogger(VenueClient.class);

    /** One room, as the Venue API describes it. */
    record Room(String name, Integer capacity) {
    }

    private final RestClient client;

    public VenueClient(RestClient.Builder builder, VenueProperties properties) {
        SimpleClientHttpRequestFactory requests = new SimpleClientHttpRequestFactory();
        requests.setConnectTimeout(500);
        requests.setReadTimeout(500);
        this.client = builder
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requests)
                .build();
    }

    // ---- STATION 09 - BLOCK F - "The new API isn't live yet" -------------------
    // Found with : the WireMock plugin. Generate WireMock Stubs from the gutter of
    //              specs/venue-api.yaml, Run WireMock, and point conftrack.venue.base-url
    //              at it. The WireMock console printed "Request was not matched" with
    //              the closest stub, GET /v1/rooms, beside what we sent, GET /rooms.
    // Fixed by   : "/rooms" -> "/v1/rooms", the path the spec actually publishes.
    // Why        : an API that exists only as a document. The stub is the contract,
    //              running, so the path bug shows up now instead of on go-live day.
    // Notes      : docs/stations/STATION-09.md
    /**
     * How many people each room seats, keyed by room name. Empty if the Venue API
     * cannot be reached, so a missing API never takes the rest of the app down.
     */
    public Map<String, Integer> capacities() {
        try {
            List<Room> rooms = client.get()
                    .uri("/v1/rooms")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Room>>() {
                    });
            Map<String, Integer> byName = new HashMap<>();
            if (rooms != null) {
                for (Room room : rooms) {
                    byName.put(room.name(), room.capacity());
                }
            }
            return byName;
        } catch (RestClientException e) {
            log.warn("Venue API call failed, so room capacities are unknown: {}", e.getMessage());
            return Map.of();
        }
    }
}
