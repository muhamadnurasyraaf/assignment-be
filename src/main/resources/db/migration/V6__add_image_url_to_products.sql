ALTER TABLE products
    add image_url VARCHAR(255),
    add created_by NOT NULL REFERENCES users(id);

ALTER TABLE users
    ADD CONSTRAINT users_email_unique UNIQUE (email);