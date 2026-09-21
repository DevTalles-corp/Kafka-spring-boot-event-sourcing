package com.bistro.reservations.query;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationViewRepository extends JpaRepository<ReservationView, String> {
}