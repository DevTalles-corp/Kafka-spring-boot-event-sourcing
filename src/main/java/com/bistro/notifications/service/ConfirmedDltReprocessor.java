package com.bistro.notifications.service;

import com.bistro.reservations.events.ReservationConfirmed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConfirmedDltReprocessor {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            id = "confirmedDltReprocessor",
            topics = "reservation-confirmed-dlt",
            groupId = "dlt-reprocessor",
            autoStartup = "false",
            properties = { "auto.offset.reset=earliest" }
    )
    public void reprocess(ConsumerRecord<String, ReservationConfirmed> record){

        ReservationConfirmed event = record.value();

        log.info("Reprocesando desde DLT: reserva {} → reenvío a reservation-confirmed", event.reservationId());

        kafkaTemplate.send("reservation-confirmed", record.key(), event);

    }
}
















