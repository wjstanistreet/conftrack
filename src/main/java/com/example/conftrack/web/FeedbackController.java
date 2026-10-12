package com.example.conftrack.web;

import com.example.conftrack.service.FeedbackService;
import com.example.conftrack.web.dto.FeedbackView;
import com.example.conftrack.web.dto.NewFeedback;
import com.example.conftrack.web.dto.SessionFeedback;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Reading and adding the feedback for one session. */
@RestController
@RequestMapping("/api/sessions/{id}/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public SessionFeedback forSession(@PathVariable Long id) {
        return feedbackService.feedbackFor(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackView add(@PathVariable Long id, @Valid @RequestBody NewFeedback submitted) {
        return feedbackService.record(id, submitted);
    }
}
