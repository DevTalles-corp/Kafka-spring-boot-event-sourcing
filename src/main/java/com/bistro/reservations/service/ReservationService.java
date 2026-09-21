package com.bistro.reservations.service;

import com.bistro.reservations.ReservationNotFoundException;
import com.bistro.reservations.controller.ReservationMapper;
import com.bistro.reservations.controller.ReservationRequest;
import com.bistro.reservations.controller.ReservationResponse;
import com.bistro.reservations.controller.ReservationStatusResponse;
import com.bistro.reservations.events.*;
import com.bistro.reservations.eventstore.ReservationEvent;
import com.bistro.reservations.eventstore.ReservationEventStore;
import com.bistro.reservations.history.ReservationStateChanged;
import com.bistro.reservations.model.*;
import com.bistro.reservations.outbox.OutboxMessage;
import com.bistro.reservations.outbox.OutboxRepository;
import com.bistro.reservations.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ReservationService {

    private final ReservationEventStore eventStore;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonMapper jsonMapper;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void confirm( Long reservationId, Long tableId, String tableNumber){

        Reservation reservation = eventStore.load(reservationId);

        if(reservation.getReservationCode()==null){
            throw new IllegalArgumentException("Reserva no encontrada: " + reservationId);
        }

        ReservationStatus previousStatus = reservation.getStatus();

        eventStore.append(reservationId,reservation.getReservationCode(),
                            new ReservationEvent.Confirmed(tableId, tableNumber));

        log.info("Reserva {} CONFIRMED con mesa {}",
                reservation.getReservationCode(), tableNumber);

        ReservationConfirmedV2 confirmedV2 = new ReservationConfirmedV2(
                reservation.getId(),
                reservation.getReservationCode(),
                reservation.getCustomerEmail(),
                reservation.getCustomerName(),
                tableNumber,
                reservation.getReservationTime(),
                LocalDateTime.now());

        kafkaTemplate.send("reservation-confirmed-v2", String.valueOf(reservation.getId()), confirmedV2);

        eventPublisher.publishEvent(new ReservationStateChanged(
                reservation.getId(),
                reservation.getReservationCode(),
                previousStatus,
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        ));

    }

    @Transactional
    public void reject(Long reservationId, String reason){
        Reservation reservation = eventStore.load(reservationId);

        if (reservation.getReservationCode() == null) {
            throw new IllegalArgumentException("Reserva no encontrada: " + reservationId);
        }

        ReservationStatus previousStatus = reservation.getStatus();

        // append: agregamos el hecho "rechazada" (antes era setStatus + save)
        eventStore.append(reservationId, reservation.getReservationCode(),
                new ReservationEvent.Rejected(reason));

        log.info("Reserva {} REJECTED: {}",
                reservation.getReservationCode(), reason);

        ReservationRejected rejected = new ReservationRejected(
                reservation.getId(),
                reservation.getReservationCode(),
                reservation.getCustomerEmail(),
                reason,
                LocalDateTime.now());

        kafkaTemplate.send("reservation-rejected",
                String.valueOf(reservation.getId()), rejected);

        eventPublisher.publishEvent(new ReservationStateChanged(
                reservation.getId(),
                reservation.getReservationCode(),
                previousStatus,
                ReservationStatus.REJECTED,
                LocalDateTime.now()
        ));
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        Long reservationId = eventStore.nextReservationId();
        String reservationCode = generateUniqueReservationCode();

        ReservationEvent.Created created = new ReservationEvent.Created(
                reservationId,
                reservationCode,
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getReservationTime(),
                request.getPartySize()
        );

        eventStore.append(reservationId, reservationCode, created);

        ReservationCreated event = new ReservationCreated(
                reservationId,
                request.getPartySize(),
                LocalDateTime.now());

        String payload = jsonMapper.writeValueAsString(event);

        OutboxMessage message = OutboxMessage.builder()
                .topic("reservation-created")
                .messageKey(String.valueOf(reservationId))
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .build();

        outboxRepository.save(message);

        eventPublisher.publishEvent(new ReservationStateChanged(
                reservationId,
                reservationCode,
                null,
                ReservationStatus.PENDING,
                LocalDateTime.now()));

        return new ReservationResponse(reservationCode, ReservationStatus.PENDING, null);
    }

    @Transactional(readOnly = true)
    public ReservationStatusResponse getReservationStatus(String reservationCode) {
        Reservation reservation = eventStore.loadByCode(reservationCode);

        if(reservation.getReservationCode()==null){
            throw new ReservationNotFoundException(reservationCode);
        }
        return reservationMapper.toStatusResponse(reservation);
    }

    private String generateUniqueReservationCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
       return "RES-" + date + "-" + uuid;
    }

    @Transactional
    public void cancel(String reservationCode){

        Reservation reservation = eventStore.loadByCode(reservationCode);

        if (reservation.getReservationCode() == null) {
            throw new IllegalArgumentException("Reserva no encontrada: " + reservationCode);
        }

        ReservationStatus previousStatus = reservation.getStatus();

        // la guarda de negocio se mantiene igual que antes
        if (previousStatus == ReservationStatus.REJECTED || previousStatus == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("No se puede cancelar una reserva en estado " + previousStatus);
        }

        // append: agregamos el hecho "cancelada" (antes era setStatus + save)
        eventStore.append(reservation.getId(), reservationCode, new ReservationEvent.Cancelled());


        log.info("Reserva {} CANCELLED", reservation.getReservationCode());

        ReservationCancelled cancelled = new ReservationCancelled(
                reservation.getId(),
                reservation.getReservationCode(),
                reservation.getCustomerEmail(),
                LocalDateTime.now());

        kafkaTemplate.send("reservation-cancelled", String.valueOf(reservation.getId()), cancelled);

        eventPublisher.publishEvent(new ReservationStateChanged(
                reservation.getId(),
                reservation.getReservationCode(),
                previousStatus,
                ReservationStatus.CANCELLED,
                LocalDateTime.now()));

    }
}




















