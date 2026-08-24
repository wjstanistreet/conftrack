package com.example.conftrack.repo;

import com.example.conftrack.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findBySessionIdOrderBySubmittedAtDesc(Long sessionId);

    long countBySessionId(Long sessionId);
}
