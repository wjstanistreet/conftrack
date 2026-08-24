package com.example.conftrack.web.dto;

import java.time.LocalDateTime;

public record SessionListItem(
        Long id,
        String title,
        String speaker,
        String room,
        LocalDateTime startsAt) {
}
