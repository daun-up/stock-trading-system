CREATE TABLE account_balances (
    account_id UUID PRIMARY KEY,
    available_cash NUMERIC(19, 4) NOT NULL,
    reserved_cash NUMERIC(19, 4) NOT NULL
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    account_id UUID NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side VARCHAR(10) NOT NULL,
    order_type VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    quantity NUMERIC(19, 4) NOT NULL,
    price NUMERIC(19, 4),
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_orders_account_id_created_at ON orders (account_id, created_at DESC);
CREATE INDEX idx_orders_symbol_created_at ON orders (symbol, created_at DESC);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    published BOOLEAN NOT NULL
);

CREATE INDEX idx_outbox_events_published_created_at ON outbox_events (published, created_at);

INSERT INTO account_balances (account_id, available_cash, reserved_cash)
VALUES ('11111111-1111-1111-1111-111111111111', 10000000.0000, 0.0000);
