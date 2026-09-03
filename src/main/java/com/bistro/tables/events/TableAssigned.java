package com.bistro.tables.events;

import java.time.LocalDateTime;

public record TableAssigned(
        Long reservationId,
        Long tableId,
        String tableNumber,
        LocalDateTime occurredAt
) {
}
