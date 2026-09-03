package com.bistro.reservations.events;

import java.time.LocalDateTime;

public record ReservationRejected(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String reason,
        LocalDateTime occurredAt
) {
}
