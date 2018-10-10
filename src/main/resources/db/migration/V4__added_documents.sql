CREATE TABLE main.documents
(
  id           bigint  NOT NULL,
  file         text    NOT NULL,
  archived     boolean not null default false,
  create_on    date    not null default now(),
  last_update  date,
  CONSTRAINT documents_pkey PRIMARY KEY (id),
  employee_id  bigint REFERENCES main.employees (id),
  equipment_id bigint REFERENCES main.equipment (id) CHECK (equipment_id IS NOT NULL or employee_id IS NOT NULL)
);

ALTER TABLE main.documents
  OWNER to ledoc;

-- auto-generated definition
create sequence document_seq
  increment by 50;

alter sequence document_seq
  owner to ledoc;

ALTER TABLE main.employees
  DROP COLUMN avatar;
ALTER TABLE main.employees
  ADD COLUMN avatar text;

ALTER TABLE main.equipment
  DROP COLUMN avatar;
ALTER TABLE main.equipment
  ADD COLUMN avatar text;


