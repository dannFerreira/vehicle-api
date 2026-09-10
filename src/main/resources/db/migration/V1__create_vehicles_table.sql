CREATE TABLE vehicles (
      id BIGSERIAL PRIMARY KEY,
      brand VARCHAR(100) NOT NULL,
      model VARCHAR(100) NOT NULL,
      year INTEGER NOT NULL,
      color VARCHAR(50) NOT NULL,
      price NUMERIC(12, 2) NOT NULL,
      status VARCHAR(20) NOT NULL,
      created_at TIMESTAMP NOT NULL,
      updated_at TIMESTAMP NOT NULL
);