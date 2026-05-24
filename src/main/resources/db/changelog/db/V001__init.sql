--liquibase formatted sql

--changeset seungwon:1
CREATE TABLE IF NOT EXISTS p_deliveries (
    delivery_id      UUID        PRIMARY KEY,
    delivery_type    VARCHAR(50) NOT NULL,
    carrier_type     VARCHAR(50) NOT NULL,
    order_id         UUID,
    product_id       UUID        NOT NULL,
    sender_id        UUID        NOT NULL,
    receiver_id      UUID        NOT NULL,
    center_id        UUID,
    status           VARCHAR(50) NOT NULL,
    batch_id         UUID,
    tracking_number  VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL,
    shipped_at       TIMESTAMPTZ,
    delivered_at     TIMESTAMPTZ,
    cancelled_at     TIMESTAMPTZ,
    failure_reason   VARCHAR(255)
);

--changeset seungwon:2
CREATE TABLE IF NOT EXISTS p_delivery_outbox (
    outbox_id          UUID         PRIMARY KEY,
    topic       VARCHAR(255) NOT NULL,
    message_key VARCHAR(255) NOT NULL,
    payload     TEXT         NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    retry_count INTEGER      DEFAULT 0,
    last_error  TEXT,
    created_at  TIMESTAMPTZ  NOT NULL,
    published_at TIMESTAMPTZ
);

--changeset seungwon:3
CREATE TABLE IF NOT EXISTS p_delivery_batches (
    delivery_batch_id UUID        PRIMARY KEY,
    started_at        TIMESTAMPTZ NOT NULL,
    status            VARCHAR(50) NOT NULL,
    total_count       INTEGER     DEFAULT 0,
    success_count     INTEGER     DEFAULT 0,
    failed_count      INTEGER     DEFAULT 0,
    completed_at      TIMESTAMPTZ
);

--changeset seungwon:4
CREATE TABLE IF NOT EXISTS p_processed_events (
    event_key    VARCHAR(255) PRIMARY KEY,
    processed_at TIMESTAMPTZ  NOT NULL
);
