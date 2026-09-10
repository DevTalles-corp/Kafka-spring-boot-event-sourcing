package com.bistro.tables.idempotency;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_events")
@Getter
@NoArgsConstructor
public class ProcessedEvent {

    @Id
    private Long reservationId;

    @Column(name = "processed_at", nullable = false, updatable = false)
    private LocalDateTime processedAt;

    public ProcessedEvent(Long reservationId) {
        this.reservationId = reservationId;
    }

    @PrePersist
    protected void onProcess() {
        this.processedAt = LocalDateTime.now();
    }
}