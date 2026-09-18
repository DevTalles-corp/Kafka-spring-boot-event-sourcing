package com.bistro.reservations.events;

import java.time.LocalDateTime;

public record ReservationConfirmed(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String customerName,
        String tableNumber,
        LocalDateTime reservationTime,
        LocalDateTime occurredAt
) {
}
