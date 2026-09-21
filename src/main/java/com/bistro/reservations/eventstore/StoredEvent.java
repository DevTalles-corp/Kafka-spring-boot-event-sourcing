package com.bistro.reservations.eventstore;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StoredEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;
    private String reservationCode;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String data;

    private LocalDateTime occurredAt;
}