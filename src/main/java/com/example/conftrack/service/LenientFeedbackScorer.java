package com.example.conftrack.service;

import com.example.conftrack.config.FeedbackProperties;
import com.example.conftrack.domain.Feedback;

import java.util.List;

/**
 * Ignores unhappy ratings entirely. Written for a demo, never intended for real use,
 * and deliberately not a @Component - it only exists if something declares it as a bean.
 */
public class LenientFeedbackScorer implements FeedbackScorer {

    private final FeedbackProperties properties;

    public LenientFeedbackScorer(FeedbackProperties properties) {
        this.properties = properties;
    }

    @Override
    public double score(List<Feedback> feedback) {
        double average = feedback.stream()
                .mapToInt(Feedback::getRating)
                .filter(rating -> rating > properties.getMinimumRating())
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }

    @Override
    public String describe() {
        return "lenient(drops ratings <= " + properties.getMinimumRating() + ")";
    }
}
