ALTER TABLE stock_movements
    ADD user_id UUID NOT NULL REFERENCES users(id);