package com.bistro.reservations.query;

import com.bistro.reservations.model.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_view")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReservationView {

    @Id
    private String reservationCode;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String customerName;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private Long assignedTableId;

    private LocalDateTime updatedAt;   // cuándo se actualizó la vista
}