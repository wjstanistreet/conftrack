package com.example.conftrack.repo;

import com.example.conftrack.domain.Moderation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRepository extends JpaRepository<Moderation, Long> {
}
