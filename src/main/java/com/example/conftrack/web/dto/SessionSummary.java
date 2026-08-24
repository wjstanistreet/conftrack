package com.example.conftrack.web.dto;

/**
 * One row of the dashboard. The field names here are the JSON field names
 * the TypeScript dashboard reads.
 */
public record SessionSummary(
        Long sessionId,
        String title,
        String speaker,
        long feedbackCount,
        double averageRating,
        double score) {
}
