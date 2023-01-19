CREATE TABLE Currency (
    code text PRIMARY KEY,
    name text NOT NULL,
    symbol text NOT NULL
);

CREATE TABLE Provider (
    id text PRIMARY KEY,
    name text NOT NULL
);

CREATE TABLE Provider_x_Currency (
    provider_id text NOT NULL,
    currency_code text NOT NULL,
    PRIMARY KEY (provider_id, currency_code),
    FOREIGN KEY (provider_id) REFERENCES Provider (id),
    FOREIGN KEY (currency_code) REFERENCES Currency (code)
);

