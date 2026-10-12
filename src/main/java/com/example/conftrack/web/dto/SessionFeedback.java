package com.example.conftrack.web.dto;

import java.util.List;

public record SessionFeedback(
        Long sessionId,
        String title,
        int feedbackCount,
        double averageRating,
        List<FeedbackView> feedback) {
}
