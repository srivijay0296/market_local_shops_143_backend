CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

INSERT INTO categories (name) VALUES ('groceries') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('electronics') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('clothing') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('accessories') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('home decor') ON CONFLICT DO NOTHING;
