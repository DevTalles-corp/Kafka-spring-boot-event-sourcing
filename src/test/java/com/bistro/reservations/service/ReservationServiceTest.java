package com.bistro.reservations.service;

import com.bistro.reservations.controller.ReservationRequest;
import com.bistro.reservations.controller.ReservationResponse;
import com.bistro.reservations.model.ReservationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void shouldCreateReservationInPendingState() {
        ReservationRequest request = ReservationRequest.builder()
                .customerName("Ana García")
                .customerEmail("ana@example.com")
                .reservationTime(LocalDateTime.of(2026, 8, 20, 19, 30))
                .partySize(4)
                .build();

        ReservationResponse response = reservationService.createReservation(request);

        assertThat(response.getReservationCode()).isNotBlank();
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
        assertThat(response.getAssignedTableId()).isNull();
    }

    @Test
    void shouldPersistTerminalStatus() {
        ReservationRequest request = ReservationRequest.builder()
                .customerName("María Pérez")
                .customerEmail("maria@example.com")
                .reservationTime(LocalDateTime.of(2026, 8, 20, 21, 0))
                .partySize(2)
                .build();

        ReservationResponse response = reservationService.createReservation(request);

        long count = reservationRepository.count();
        assertThat(count).isEqualTo(1);
        assertThat(reservationRepository.findByReservationCode(response.getReservationCode()))
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r.getStatus()).isEqualTo(ReservationStatus.PENDING));
    }
}
