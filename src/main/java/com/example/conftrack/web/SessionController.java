package com.example.conftrack.web;

import com.example.conftrack.service.FeedbackService;
import com.example.conftrack.web.dto.SessionDetail;
import com.example.conftrack.web.dto.SessionListItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** The programme: what is on, and how one session did. */
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final FeedbackService feedbackService;

    public SessionController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public List<SessionListItem> list() {
        return feedbackService.listSessions();
    }

    @GetMapping("/{id}")
    public SessionDetail one(@PathVariable Long id) {
        return feedbackService.detail(id);
    }
}
