package com.bistro.reservations.eventstore;

import com.bistro.reservations.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReservationEventStore {

    private final StoredEventRepository repository;
    private final JsonMapper jsonMapper;

    public Long nextReservationId(){
        return repository.nextReservationId();
    }

    public void append (Long reservationId, String reservationCode, ReservationEvent event){
        StoredEvent row = StoredEvent.builder()
                .reservationId(reservationId)
                .reservationCode(reservationCode)
                .eventType(event.getClass().getSimpleName())
                //{"tableId":5,"tableNumber":"M-05"}
                .data(jsonMapper.writeValueAsString(event))
                .occurredAt(LocalDateTime.now())
                .build();

        repository.save(row);
    }

    public Reservation load(Long reservationId){
        List<ReservationEvent> events = repository.findByReservationIdOrderByIdAsc(reservationId)
                .stream()
                .map(this::toEvent).toList();
        return Reservation.rebuild(events);
    }

    public Reservation loadByCode(String reservationCode) {
        List<ReservationEvent> events = repository.findByReservationCodeOrderByIdAsc(reservationCode)
                .stream().map(this::toEvent).toList();
        return Reservation.rebuild(events);
    }

    private ReservationEvent toEvent(StoredEvent row) {
        return switch (row.getEventType()) {
            case "Created"   -> jsonMapper.readValue(row.getData(), ReservationEvent.Created.class);
            case "Confirmed" -> jsonMapper.readValue(row.getData(), ReservationEvent.Confirmed.class);
            case "Rejected"  -> jsonMapper.readValue(row.getData(), ReservationEvent.Rejected.class);
            case "Cancelled" -> jsonMapper.readValue(row.getData(), ReservationEvent.Cancelled.class);
            default -> throw new IllegalStateException("Tipo de hecho desconocido: " + row.getEventType());
        };
    }

}





















