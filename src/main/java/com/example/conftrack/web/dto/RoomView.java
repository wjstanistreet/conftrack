package com.example.conftrack.web.dto;

/** One room on the programme. capacity is null when the Venue API did not say. */
public record RoomView(
        String name,
        Integer capacity,
        long sessionCount) {
}
