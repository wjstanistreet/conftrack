package com.example.conftrack.service;

import com.example.conftrack.domain.Feedback;
import com.example.conftrack.domain.Moderation;
import com.example.conftrack.domain.Session;
import com.example.conftrack.repo.FeedbackRepository;
import com.example.conftrack.repo.SessionRepository;
import com.example.conftrack.web.dto.FeedbackView;
import com.example.conftrack.web.dto.NewFeedback;
import com.example.conftrack.web.dto.SessionDetail;
import com.example.conftrack.web.dto.SessionFeedback;
import com.example.conftrack.web.dto.SessionListItem;
import com.example.conftrack.web.dto.SessionSummary;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeedbackService {

    private final SessionRepository sessions;
    private final FeedbackRepository feedback;
    private final FeedbackScorer scorer;

    public FeedbackService(SessionRepository sessions,
                           FeedbackRepository feedback,
                           FeedbackScorer scorer) {
        this.sessions = sessions;
        this.feedback = feedback;
        this.scorer = scorer;
    }

    @Transactional(readOnly = true)
    public List<SessionListItem> listSessions() {
        List<SessionListItem> items = new ArrayList<>();
        for (Session session : sessions.findAllOrdered()) {
            items.add(new SessionListItem(
                    session.getId(),
                    session.getTitle(),
                    session.getSpeaker(),
                    session.getRoom(),
                    session.getStartsAt()));
        }
        return items;
    }

    /** Every row the dashboard draws, in one call. */
    @Transactional(readOnly = true)
    public List<SessionSummary> summarise() {
        List<SessionSummary> rows = new ArrayList<>();
        for (Session session : sessions.findAllForSummary()) {
            List<Feedback> given = session.getFeedback();
            rows.add(new SessionSummary(
                    session.getId(),
                    session.getTitle(),
                    session.getSpeaker(),
                    given.size(),
                    averageOf(given),
                    scorer.score(given)));
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public SessionDetail detail(Long sessionId) {
        Session session = requireSession(sessionId);
        List<Feedback> given = session.getFeedback();
        return new SessionDetail(
                session.getId(),
                session.getTitle(),
                session.getSpeaker(),
                session.getRoom(),
                session.getStartsAt(),
                given.size(),
                averageOf(given),
                scorer.score(given));
    }

    @Transactional(readOnly = true)
    public SessionFeedback feedbackFor(Long sessionId) {
        Session session = requireSession(sessionId);
        List<Feedback> given = feedback.findBySessionIdOrderBySubmittedAtDesc(sessionId);

        double average = given.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .getAsDouble();

        List<FeedbackView> views = new ArrayList<>();
        for (Feedback item : given) {
            views.add(new FeedbackView(
                    item.getId(),
                    item.getAttendeeEmail(),
                    item.getRating(),
                    StringEscapeUtils.escapeHtml4(item.getComments()),
                    item.getSubmittedAt(),
                    moderationOf(item)));
        }

        return new SessionFeedback(
                session.getId(),
                session.getTitle(),
                views.size(),
                Math.round(average * 100.0) / 100.0,
                views);
    }

    @Transactional
    public FeedbackView record(Long sessionId, NewFeedback submitted) {
        Session session = requireSession(sessionId);

        Feedback item = new Feedback();
        item.setSession(session);
        item.setAttendeeEmail(submitted.attendeeEmail());
        item.setRating(submitted.rating());
        item.setComments(submitted.comments());
        item.setSubmittedAt(LocalDateTime.now());

        Feedback saved = feedback.save(item);
        return new FeedbackView(
                saved.getId(),
                saved.getAttendeeEmail(),
                saved.getRating(),
                StringEscapeUtils.escapeHtml4(saved.getComments()),
                saved.getSubmittedAt(),
                moderationOf(saved));
    }

    private Session requireSession(Long sessionId) {
        return sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No session with id " + sessionId));
    }

    private double averageOf(List<Feedback> given) {
        double average = given.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }

    private String moderationOf(Feedback item) {
        Moderation moderation = item.getModeration();
        return moderation == null ? "UNREVIEWED" : moderation.getStatus();
    }
}
