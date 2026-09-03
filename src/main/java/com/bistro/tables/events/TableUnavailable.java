package com.bistro.tables.events;

import java.time.LocalDateTime;

public record TableUnavailable(
        Long reservationId,
        String reason,
        LocalDateTime occurredAt
) {
}
