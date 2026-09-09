package com.bistro.reservations.outbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    List<OutboxMessage> findByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
