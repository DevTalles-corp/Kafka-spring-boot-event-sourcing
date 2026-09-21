CREATE TABLE reservation_view (
        reservation_code   VARCHAR(255) PRIMARY KEY,
        status             VARCHAR(50),
        customer_name      VARCHAR(255),
        reservation_time   TIMESTAMP,
        party_size         INT,
        assigned_table_id  BIGINT,
        updated_at         TIMESTAMP
);