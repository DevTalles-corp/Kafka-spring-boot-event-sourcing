package com.bistro.reservations.controller;

import com.bistro.reservations.model.Reservation;
import com.bistro.reservations.model.ReservationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class ReservationStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
    }

    @Test
    void shouldReturnStatusForExistingReservation() throws Exception {
        Reservation reservation = Reservation.builder()
                .reservationCode("RES-TEST-1234")
                .customerName("Ana García")
                .customerEmail("ana@example.com")
                .reservationTime(LocalDateTime.of(2026, 8, 20, 19, 30))
                .partySize(4)
                .status(ReservationStatus.CONFIRMED)
                .assignedTableId(2L)
                .createdAt(LocalDateTime.now())
                .build();
        reservationRepository.save(reservation);

        mockMvc.perform(get("/api/v1/reservations/RES-TEST-1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationCode").value("RES-TEST-1234"))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.assignedTableId").value(2))
                .andExpect(jsonPath("$.customerName").value("Ana García"));
    }

    @Test
    void shouldReturn404ForNonExistingReservation() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/RES-NONEXISTENT"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"));
    }
}
