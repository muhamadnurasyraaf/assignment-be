ALTER TABLE users
    ADD COLUMN company_id UUID NULL REFERENCES companies(id);
