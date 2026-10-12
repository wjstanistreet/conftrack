package com.example.conftrack.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** A moderator's decision on one piece of feedback. Most feedback has never been reviewed. */
@Entity
@Table(name = "feedback_moderation")
public class Moderation {

    @Id
    @Column(name = "feedback_id")
    private Long feedbackId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feedback_id")
    private Feedback feedback;

    @Column(nullable = false)
    private String status;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;

    public Long getFeedbackId() {
        return feedbackId;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
