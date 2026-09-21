package com.bistro.reservations.controller;

import com.bistro.reservations.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reservationCode}")
    public ResponseEntity<ReservationStatusResponse> getReservationStatus(
            @PathVariable String reservationCode) {
        ReservationStatusResponse response = reservationService.getReservationStatus(reservationCode);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reservationCode}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable String reservationCode){
        reservationService.cancel(reservationCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationStatusResponse>> listReservations() {
        return ResponseEntity.ok(reservationService.listReservations());
    }


}










