package com.example.conftrack.service;

import com.example.conftrack.config.FeedbackProperties;
import com.example.conftrack.domain.Feedback;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The scorer the business actually asked for: unhappy ratings count for more,
 * so a session with a handful of angry reviews does not hide behind its average.
 */
@Component
public class WeightedFeedbackScorer implements FeedbackScorer {

    private final FeedbackProperties properties;

    public WeightedFeedbackScorer(FeedbackProperties properties) {
        this.properties = properties;
    }

    @Override
    public double score(List<Feedback> feedback) {
        if (feedback.isEmpty()) {
            return 0.0;
        }
        double weightedTotal = 0.0;
        double weightTotal = 0.0;
        for (Feedback item : feedback) {
            double weight = item.getRating() <= properties.getMinimumRating()
                    ? properties.getLowRatingWeight()
                    : 1.0;
            weightedTotal += item.getRating() * weight;
            weightTotal += weight;
        }
        return Math.round((weightedTotal / weightTotal) * 100.0) / 100.0;
    }

    @Override
    public String describe() {
        return "weighted(lowRatingWeight=" + properties.getLowRatingWeight() + ")";
    }
}
