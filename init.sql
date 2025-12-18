CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    product_info VARCHAR(255),
    status VARCHAR(255),
    card_data TEXT
);
