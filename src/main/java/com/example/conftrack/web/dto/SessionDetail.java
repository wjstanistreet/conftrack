package com.example.conftrack.web.dto;

import java.time.LocalDateTime;

public record SessionDetail(
        Long id,
        String title,
        String speaker,
        String room,
        LocalDateTime startsAt,
        long feedbackCount,
        double averageRating,
        double score) {
}
