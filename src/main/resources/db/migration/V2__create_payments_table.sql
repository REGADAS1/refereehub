CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT NOT NULL UNIQUE,
    fee_amount NUMERIC(10, 2) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_at DATE,
    kilometers NUMERIC(10, 2),
    km_rate NUMERIC(10, 2),
    night_subsidy_applied BOOLEAN NOT NULL DEFAULT FALSE,
    night_subsidy_amount NUMERIC(10, 2),
    notes TEXT,

    CONSTRAINT fk_payments_match
        FOREIGN KEY (match_id)
        REFERENCES matches(id)
        ON DELETE CASCADE
);