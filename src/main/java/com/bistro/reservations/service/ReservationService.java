package com.bistro.reservations.service;

import com.bistro.reservations.ReservationNotFoundException;
import com.bistro.reservations.controller.ReservationMapper;
import com.bistro.reservations.controller.ReservationRequest;
import com.bistro.reservations.controller.ReservationResponse;
import com.bistro.reservations.controller.ReservationStatusResponse;
import com.bistro.reservations.events.*;
import com.bistro.reservations.history.ReservationStateChanged;
import com.bistro.reservations.model.*;
import com.bistro.reservations.outbox.OutboxMessage;
import com.bistro.reservations.outbox.OutboxRepository;
import com.bistro.reservations.outbox.OutboxStatus;
import com.bistro.reservations.repository.ReservationRepository;
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

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReservationMapper reservationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final JsonMapper jsonMapper;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void confirm( Long reservationId, Long tableId, String tableNumber){

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow( () -> new IllegalArgumentException(
                        "Reserva no encontrada: " + reservationId
                ));

        ReservationStatus previousStatus = reservation.getStatus();

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setAssignedTableId(tableId);
        reservationRepository.save(reservation);

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
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalStateException(
                        "Reserva no encontrada: " + reservationId));

        ReservationStatus previousStatus = reservation.getStatus();

        reservation.setStatus(ReservationStatus.REJECTED);
        reservationRepository.save(reservation);

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
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setReservationCode(generateUniqueReservationCode());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);

        ReservationCreated event = new ReservationCreated(
                saved.getId(),
                saved.getPartySize(),
                LocalDateTime.now());

        String payload = jsonMapper.writeValueAsString(event);

        OutboxMessage message = OutboxMessage.builder()
                .topic("reservation-created")
                .messageKey(String.valueOf(saved.getId()))
                .payload(payload)
                .status(OutboxStatus.PENDING)
                .build();

        outboxRepository.save(message);

        eventPublisher.publishEvent(new ReservationStateChanged(
                saved.getId(),
                saved.getReservationCode(),
                null,
                ReservationStatus.PENDING,
                LocalDateTime.now()));

        return reservationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ReservationStatusResponse getReservationStatus(String reservationCode) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new ReservationNotFoundException(reservationCode));
        return reservationMapper.toStatusResponse(reservation);
    }

    private String generateUniqueReservationCode() {
        String code;
        do {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            code = "RES-" + date + "-" + uuid;
        } while (reservationRepository.findByReservationCode(code).isPresent());
        return code;
    }

    @Transactional
    public void cancel(String reservationCode){

        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Reserva no encontrada: " + reservationCode
                        ));

        ReservationStatus previousStatus = reservation.getStatus();

        if(previousStatus == ReservationStatus.REJECTED || previousStatus == ReservationStatus.CANCELLED){
            throw  new IllegalStateException( "No se puede cancelar una reserva en estado " + previousStatus);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

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




















