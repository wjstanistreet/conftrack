package com.example.conftrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Everything under "conftrack.venue" in application.yml binds here.
 */
@ConfigurationProperties(prefix = "conftrack.venue")
public class VenueProperties {

    /** Where Facilities' Venue API lives. The spec is in specs/venue-api.yaml. */
    private String baseUrl = "https://venue.internal.example.com";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
