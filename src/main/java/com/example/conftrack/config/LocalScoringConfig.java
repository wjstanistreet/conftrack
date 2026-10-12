package com.example.conftrack.config;

import com.example.conftrack.service.FeedbackScorer;
import com.example.conftrack.service.LenientFeedbackScorer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Scoring tweaks that only apply when the "local" profile is active.
 */
@Configuration
@Profile("local")
public class LocalScoringConfig {

    @Bean
    public FeedbackScorer weightedFeedbackScorer(FeedbackProperties properties) {
        return new LenientFeedbackScorer(properties);
    }
}
