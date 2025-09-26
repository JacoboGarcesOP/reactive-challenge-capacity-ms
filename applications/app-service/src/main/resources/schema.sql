CREATE SCHEMA IF NOT EXISTS capacity_schema;

CREATE TABLE IF NOT EXISTS capacity_schema.capacity (
    capacity_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS capacity_schema.capacity_bootcamp (
    id BIGSERIAL PRIMARY KEY,
    capacity_id BIGINT NOT NULL,
    bootcamp_id BIGINT NOT NULL,
    UNIQUE(bootcamp_id, capacity_id),
    FOREIGN KEY (capacity_id) REFERENCES capacity_schema.capacity(capacity_id) ON DELETE CASCADE
);


CREATE INDEX idx_capacity_name ON capacity_schema.capacity(name);
CREATE INDEX idx_capacity_bootcamp_capacity_id ON capacity_schema.capacity_bootcamp(bootcamp_id);