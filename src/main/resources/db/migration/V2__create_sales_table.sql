CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    buyer_id VARCHAR(100) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);