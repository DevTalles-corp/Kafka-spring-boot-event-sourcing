package com.bistro.reservations.service;

import com.bistro.tables.events.TableUnavailable;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableUnavailableListener {

    private final ReservationService reservationService;

    @KafkaListener(topics = "table-unavailable", groupId = "reservations")
    public void onTableUnavailable(TableUnavailable event){
        reservationService.reject(event.reservationId(), event.reason());
    }
}
