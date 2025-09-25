CREATE SCHEMA IF NOT EXISTS capacity_schema;

CREATE TABLE IF NOT EXISTS capacity_schema.capacity (
    capacity_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
);

CREATE INDEX idx_capacity_name ON capacity_schema.capacity(name);