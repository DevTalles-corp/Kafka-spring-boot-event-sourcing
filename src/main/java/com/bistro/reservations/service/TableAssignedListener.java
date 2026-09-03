package com.bistro.reservations.service;

import com.bistro.tables.events.TableAssigned;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TableAssignedListener {

    private final ReservationService reservationService;

    @KafkaListener(topics = "table-assigned", groupId = "reservations")
    public void onTableAssigned(TableAssigned event){
        reservationService.confirm(event.reservationId(), event.tableId(), event.tableNumber());
    }
}
