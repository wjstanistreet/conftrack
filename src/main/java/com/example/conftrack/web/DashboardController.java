package com.example.conftrack.web;

import com.example.conftrack.config.FeedbackProperties;
import com.example.conftrack.service.FeedbackService;
import com.example.conftrack.web.dto.SessionSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** What the dashboard page in src/main/resources/static calls. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final FeedbackService feedbackService;
    private final FeedbackProperties properties;

    public DashboardController(FeedbackService feedbackService, FeedbackProperties properties) {
        this.feedbackService = feedbackService;
        this.properties = properties;
    }

    @GetMapping("/summary")
    public List<SessionSummary> summary() {
        return feedbackService.summarise();
    }

    /**
     * The settings this application is actually running with, as opposed to the
     * ones somebody believes are in application.yml.
     */
    @GetMapping("/settings")
    public FeedbackProperties settings() {
        return properties;
    }
}
