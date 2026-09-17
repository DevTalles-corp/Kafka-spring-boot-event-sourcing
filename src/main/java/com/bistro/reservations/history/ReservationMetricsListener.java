package com.bistro.reservations.history;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class ReservationMetricsListener {

    private final AtomicLong stateChangeCount = new AtomicLong();

    @EventListener
    public void onReservationStateChanged(ReservationStateChanged event){
        long total = stateChangeCount.incrementAndGet();

        log.info("Métrica: cambios de estado acumulados = {} (última: {} → {})",
                total,
                event.previousStatus() == null ? "nueva" : event.previousStatus().name(),
                event.newStatus()
                );
    }
}
