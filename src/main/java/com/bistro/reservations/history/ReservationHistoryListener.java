package com.bistro.reservations.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReservationHistoryListener {

    @EventListener
    public void onReservationStateChanged(ReservationStateChanged event){
        String previous = event.previousStatus()==null
                ? "nueva"
                : event.previousStatus().name();

        log.info("Historial de {}: {} → {} (en el mismo proceso, sin Kafka)",
                event.reservationCode(), previous, event.newStatus());
    }
}
