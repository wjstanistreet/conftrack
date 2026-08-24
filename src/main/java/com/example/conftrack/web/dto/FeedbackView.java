package com.example.conftrack.web.dto;

import java.time.LocalDateTime;

public record FeedbackView(
        Long id,
        String attendeeEmail,
        int rating,
        String comments,
        LocalDateTime submittedAt) {
}
