package com.bistro.reservations.events;

import java.time.LocalDateTime;

public record ReservationCancelled(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        LocalDateTime occurredAt
) {
}
