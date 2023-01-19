CREATE TABLE Currency (
    code text PRIMARY KEY,
    name text NOT NULL,
    symbol text NOT NULL
);

CREATE TABLE Provider (
    id text PRIMARY KEY,
    name text NOT NULL
);

