package com.bistro.reservations.eventstore;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StoredEventRepository extends JpaRepository<StoredEvent, Long> {

    List<StoredEvent> findByReservationIdOrderByIdAsc(Long reservationId);
    List<StoredEvent> findByReservationCodeOrderByIdAsc(String reservationCode);

    @Query(value = "SELECT nextval('reservation_id_seq')", nativeQuery = true)
    Long nextReservationId();
}