ALTER TABLE main.employees ADD COLUMN creator_id BIGINT;
ALTER TABLE main.employees ADD CONSTRAINT employees_fk_1 FOREIGN KEY (creator_id) REFERENCES main.employees (id) ON DELETE CASCADE;
UPDATE main.employees SET creator_id=101 WHERE id=102;
UPDATE main.employees SET creator_id=102 WHERE id NOT IN (101, 102);