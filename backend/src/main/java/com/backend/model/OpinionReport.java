package com.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
public class OpinionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "opinion_id", nullable = false)
    private Opinion opinion;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    public OpinionReport() {

    }
    public OpinionReport(Opinion opinion, User user) {
        this.opinion = opinion;
        this.user = user;
    }
}
