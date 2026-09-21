package com.bistro.reservations.eventstore;

import java.time.LocalDateTime;

public sealed interface ReservationEvent {

    record Created(
            Long reservationId,
            String reservationCode,
            String customerName,
            String customerEmail,
            LocalDateTime reservationTime,
            Integer partySize
    ) implements ReservationEvent {}

    record Confirmed(Long tableId, String tableNumber) implements ReservationEvent {}

    record Rejected(String reason) implements ReservationEvent {}

    record Cancelled() implements ReservationEvent {}
}