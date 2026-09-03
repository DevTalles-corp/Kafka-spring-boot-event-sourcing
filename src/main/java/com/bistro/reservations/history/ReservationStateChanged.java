package com.bistro.reservations.history;

import com.bistro.reservations.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationStateChanged(
        Long reservationId,
        String reservationCode,
        ReservationStatus previousStatus,
        ReservationStatus newStatus,
        LocalDateTime changedAt
) {
}
