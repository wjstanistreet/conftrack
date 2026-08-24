package com.example.conftrack.service;

import com.example.conftrack.domain.Feedback;
import com.example.conftrack.domain.Moderation;
import com.example.conftrack.domain.Session;
import com.example.conftrack.repo.FeedbackRepository;
import com.example.conftrack.repo.ModerationRepository;
import com.example.conftrack.repo.SessionRepository;
import com.example.conftrack.web.dto.FeedbackView;
import com.example.conftrack.web.dto.NewFeedback;
import com.example.conftrack.web.dto.SessionDetail;
import com.example.conftrack.web.dto.SessionFeedback;
import com.example.conftrack.web.dto.SessionListItem;
import com.example.conftrack.web.dto.SessionSummary;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private static final String REJECTED = "REJECTED";

    private final SessionRepository sessions;
    private final FeedbackRepository feedback;
    private final ModerationRepository moderations;
    private final FeedbackScorer scorer;

    // ---- STATION 01 - BLOCK A - "It won't start" -------------------------------
    // Found with : Spring injection-point inspection and the bean gutter icon. The
    //              parameter below is underlined before you run anything, and the
    //              tooltip names both candidate beans. The beans diagram shows two
    //              arrows landing on FeedbackScorer.
    // Fixed by   : naming the bean we actually want. IDEA offers this via Alt+Enter.
    // Run it as  : four minutes with the Spring tool window. The person who knows
    //              Java least drives - the gutter icon does not care what you know.
    // Careful    : naming a bean is not the same as getting it. See Station 05.
    // Notes      : docs/stations/STATION-01.md
    public FeedbackService(SessionRepository sessions,
                           FeedbackRepository feedback,
                           ModerationRepository moderations,
                           @Qualifier("weightedFeedbackScorer") FeedbackScorer scorer) {
        this.sessions = sessions;
        this.feedback = feedback;
        this.moderations = moderations;
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
        List<Moderation> decisions = moderations.findAll();
        List<SessionSummary> rows = new ArrayList<>();
        for (Session session : sessions.findAllForSummary()) {
            List<Feedback> given = countable(session.getFeedback(), decisions);
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

        // ---- STATION 04 - BLOCK C - "The endpoint 500s" ------------------------
        // Found with : the Endpoints window plus the HTTP Client. Every route the app
        //              answers is listed without opening a controller; Alt+Enter on a
        //              mapping generates a request into an .http file that lives in the
        //              repository, and environment files hold the base URL and tokens.
        // Fixed by   : .getAsDouble() -> .orElse(0.0). Session 41 has no feedback yet.
        // Run it as  : QA's station. Announce it as theirs and let a QA engineer drive.
        //              The real question is not "why does it throw" but "how would you
        //              have found which id breaks?"
        // Proof      : re-run the same request - 200, feedbackCount 0, averageRating 0.
        // Notes      : docs/stations/STATION-04.md
        double average = given.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        Map<Long, String> statuses = new HashMap<>();
        List<Long> ids = given.stream().map(Feedback::getId).toList();
        for (Moderation decision : moderations.findAllById(ids)) {
            statuses.put(decision.getFeedbackId(), decision.getStatus());
        }

        List<FeedbackView> views = new ArrayList<>();
        for (Feedback item : given) {
            views.add(new FeedbackView(
                    item.getId(),
                    item.getAttendeeEmail(),
                    item.getRating(),
                    StringEscapeUtils.escapeHtml4(item.getComments()),
                    item.getSubmittedAt(),
                    statuses.getOrDefault(item.getId(), "UNREVIEWED")));
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
                "UNREVIEWED");
    }

    private Session requireSession(Long sessionId) {
        return sessions.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No session with id " + sessionId));
    }

    // ---- STATION 06 - BLOCK D - "The dashboard takes seconds" -----------------
    // Found with : the IntelliJ Profiler, attached to the running app in one click.
    //              The flame graph showed one plateau far wider than anything around
    //              it, and it was this method: "focus on method" put nearly all of it
    //              in the anyMatch lambda. Every moderation decision (~7,100) was
    //              scanned for every feedback row (~50,000): ~350 million checks.
    // Fixed by   : collecting the rejected ids into a Set once, so each row is one
    //              lookup instead of a scan. Same numbers, a fraction of the time.
    // Run it as  : show the millisecond readout on the dashboard page before and
    //              after. Nobody needs to read Java - the exercise is "find the wide
    //              bit", and List versus Set is the same idea in every language.
    // Honest     : the production version asks the database for the rejected ids,
    //              or for the counts and averages, instead of loading every row.
    // Notes      : docs/stations/STATION-06.md
    /** Feedback a moderator rejected does not count towards a session's numbers. */
    private List<Feedback> countable(List<Feedback> given, List<Moderation> decisions) {
        Set<Long> rejected = decisions.stream()
                .filter(decision -> REJECTED.equals(decision.getStatus()))
                .map(Moderation::getFeedbackId)
                .collect(Collectors.toSet());
        List<Feedback> kept = new ArrayList<>();
        for (Feedback item : given) {
            if (!rejected.contains(item.getId())) {
                kept.add(item);
            }
        }
        return kept;
    }

    private double averageOf(List<Feedback> given) {
        double average = given.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }
}
