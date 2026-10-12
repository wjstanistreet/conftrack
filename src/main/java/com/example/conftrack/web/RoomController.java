package com.example.conftrack.web;

import com.example.conftrack.domain.Session;
import com.example.conftrack.repo.SessionRepository;
import com.example.conftrack.venue.VenueClient;
import com.example.conftrack.web.dto.RoomView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** The rooms the programme uses, and how many people each one holds. */
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final SessionRepository sessions;
    private final VenueClient venue;

    public RoomController(SessionRepository sessions, VenueClient venue) {
        this.sessions = sessions;
        this.venue = venue;
    }

    @GetMapping
    public List<RoomView> list() {
        Map<String, Long> sessionCounts = new TreeMap<>();
        for (Session session : sessions.findAllOrdered()) {
            if (session.getRoom() != null) {
                sessionCounts.merge(session.getRoom(), 1L, Long::sum);
            }
        }
        Map<String, Integer> capacities = venue.capacities();
        return sessionCounts.entrySet().stream()
                .map(entry -> new RoomView(entry.getKey(), capacities.get(entry.getKey()), entry.getValue()))
                .toList();
    }
}
