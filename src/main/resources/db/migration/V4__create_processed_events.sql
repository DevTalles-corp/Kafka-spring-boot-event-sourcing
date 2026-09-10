CREATE TABLE IF NOT EXISTS processed_events (
        reservation_id BIGINT PRIMARY KEY,
        processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);