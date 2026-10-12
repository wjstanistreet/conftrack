package com.example.conftrack.service;

import com.example.conftrack.domain.Feedback;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The original scorer: a plain mean. Kept around because two internal reports
 * still quote its numbers.
 */
@Component
public class SimpleFeedbackScorer implements FeedbackScorer {

    @Override
    public double score(List<Feedback> feedback) {
        double average = feedback.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }

    @Override
    public String describe() {
        return "simple(mean)";
    }
}
