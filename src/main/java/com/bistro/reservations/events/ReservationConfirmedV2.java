package com.bistro.reservations.events;

import java.time.LocalDateTime;

public record ReservationConfirmedV2(
        Long reservationId,
        String reservationCode,
        String customerEmail,
        String customerName,
        String assignedTable,
        LocalDateTime reservationTime,
        LocalDateTime occurredAt
) {
}
