package com.example.conftrack.repo;

import com.example.conftrack.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    @Query("select s from Session s order by s.startsAt")
    List<Session> findAllOrdered();

    /**
     * Loads every session for the dashboard summary.
     */
    @Query("select s from Session s order by s.startsAt")
    List<Session> findAllForSummary();
}
