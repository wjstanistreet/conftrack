package com.example.conftrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Everything under "conftrack.feedback" in application.yml binds here.
 */
@ConfigurationProperties(prefix = "conftrack.feedback")
public class FeedbackProperties {

    /** Ratings at or below this value count as unhappy feedback. */
    private int minimumRating = 1;

    /** How many days of feedback the dashboard considers recent. */
    private int recentDays = 30;

    /** How much more an unhappy rating counts than a happy one. */
    private double lowRatingWeight = 1.5;

    public int getMinimumRating() {
        return minimumRating;
    }

    public void setMinimumRating(int minimumRating) {
        this.minimumRating = minimumRating;
    }

    public int getRecentDays() {
        return recentDays;
    }

    public void setRecentDays(int recentDays) {
        this.recentDays = recentDays;
    }

    public double getLowRatingWeight() {
        return lowRatingWeight;
    }

    public void setLowRatingWeight(double lowRatingWeight) {
        this.lowRatingWeight = lowRatingWeight;
    }
}
