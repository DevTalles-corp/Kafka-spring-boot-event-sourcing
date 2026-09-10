package com.bistro.tables.service;

import com.bistro.reservations.events.ReservationCreated;
import com.bistro.tables.events.TableAssigned;
import com.bistro.tables.events.TableUnavailable;
import com.bistro.tables.idempotency.ProcessedEvent;
import com.bistro.tables.idempotency.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationCreatedListener {

    private final TableService tableService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProcessedEventRepository processedEventRepository;

    @KafkaListener(topics = "reservation-created" , groupId = "tables")
    public void onReservationCreated(ReservationCreated event){

        if(processedEventRepository.existsById(event.reservationId())){
            log.info("Reserva {} ya procesada, se ignora el evento repetido",
                    event.reservationId());
            return;
        }

        tableService.assignTableFor(event.partySize()).ifPresentOrElse(
                table -> {
                    log.info("Reserva {} → mesa {} ({} lugares) asignada",
                            event.reservationId(), table.getTableNumber(), table.getCapacity());

                    TableAssigned assigned = new TableAssigned(
                            event.reservationId(),
                            table.getId(),
                            table.getTableNumber(),
                            LocalDateTime.now()
                    );

                    kafkaTemplate.send("table-assigned", String.valueOf(event.reservationId()), assigned);

                },

                () -> {
                    log.info("Reserva {} → sin mesa para {} personas",
                            event.reservationId(), event.partySize());

                    TableUnavailable unavailable = new TableUnavailable(
                            event.reservationId(),
                            "Sin mesa disponible para " + event.partySize() + " personas",
                            LocalDateTime.now()
                    );
                     kafkaTemplate.send("table-unavailable", String.valueOf(event.reservationId()), unavailable);
                });

        processedEventRepository.save(new ProcessedEvent(event.reservationId()));
    }
}













