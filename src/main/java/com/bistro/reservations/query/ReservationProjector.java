package com.bistro.reservations.query;

import com.bistro.reservations.eventstore.ReservationEventStore;
import com.bistro.reservations.history.ReservationStateChanged;
import com.bistro.reservations.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReservationProjector {

    private final ReservationEventStore eventStore;
    private final ReservationViewRepository viewRepository;

    @EventListener
    public void on(ReservationStateChanged event){

        Reservation reservation = eventStore.loadByCode(event.reservationCode());

        ReservationView view = ReservationView.builder()
                .reservationCode(reservation.getReservationCode())
                .status(reservation.getStatus())
                .customerName(reservation.getCustomerName())
                .reservationTime(reservation.getReservationTime())
                .partySize(reservation.getPartySize())
                .assignedTableId(reservation.getAssignedTableId())
                .updatedAt(LocalDateTime.now())
                .build();

        viewRepository.save(view);

        log.info("Vista de {} actualizada a {}", view.getReservationCode(), view.getStatus());
    }




}




















