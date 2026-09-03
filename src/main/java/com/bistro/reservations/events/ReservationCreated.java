package com.bistro.reservations.events;

import java.time.LocalDateTime;

public record ReservationCreated(
        Long reservationId,
        Integer partySize,
        LocalDateTime occurredAt
) {
}
