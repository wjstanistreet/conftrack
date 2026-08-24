package com.example.conftrack.service;

import com.example.conftrack.domain.Feedback;

import java.util.List;

/**
 * Turns a pile of ratings into the single number the dashboard shows.
 * There is more than one opinion in this company about how that should work,
 * which is why there is more than one implementation.
 */
public interface FeedbackScorer {

    double score(List<Feedback> feedback);

    /** Short name of this strategy, for logs and debugging. */
    String describe();
}
