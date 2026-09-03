package com.bistro.notifications.service;

import com.bistro.reservations.events.ReservationConfirmed;
import com.bistro.reservations.events.ReservationRejected;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = {"reservation-confirmed", "reservation-rejected"}, groupId = "notifications")
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @KafkaHandler
    public void onConfirmed(ReservationConfirmed event){
        notificationService.notifyConfirmed(event.customerEmail(), event.reservationCode(), event.tableNumber());
    }

    @KafkaHandler
    public void onRejected(ReservationRejected event){
        notificationService.notifyRejected(event.customerEmail(), event.reservationCode(), event.reason());
    }

}
