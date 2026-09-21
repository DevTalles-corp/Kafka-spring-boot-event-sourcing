package com.bistro.notifications.service;

import com.bistro.reservations.events.ReservationCancelled;
import com.bistro.reservations.events.ReservationConfirmedV2;
import com.bistro.reservations.events.ReservationRejected;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"reservation-confirmed-v2", "reservation-rejected", "reservation-cancelled"}, groupId = "notifications")
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaHandler
    public void onConfirmed(ReservationConfirmedV2 event){
        //throw new RuntimeException("Fallo simulado al procesar la confirmación");
        notificationService.notifyConfirmed(event.customerEmail(), event.customerName(),
                                            event.reservationCode(), event.assignedTable());
    }

    @KafkaHandler
    public void onRejected(ReservationRejected event){
        notificationService.notifyRejected(event.customerEmail(), event.reservationCode(), event.reason());
    }

    @KafkaHandler
    public void onCancelled(ReservationCancelled event){
        notificationService.notifyCancelled(event.customerEmail(), event.reservationCode());
    }

}
