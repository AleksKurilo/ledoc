-- create table supplier_location
CREATE TABLE main.supplier_location
(
  location_id BIGINT NOT NULL
    CONSTRAINT fkrnco98d1bidjargw2ut3lae1
      REFERENCES suppliers
      ON UPDATE CASCADE
      ON DELETE CASCADE,
  supplier_id BIGINT NOT NULL
    CONSTRAINT fkb5phnbjvijodfynsp0us8mine
      REFERENCES suppliers,
  CONSTRAINT supplier_location_pkey
    PRIMARY KEY (location_id, supplier_id)
);

alter table main.supplier_location
  owner to ledoc;


-- add columns to supplier
ALTER TABLE main.suppliers
  ADD COLUMN review BOOLEAN default false not null;

ALTER TABLE main.suppliers
  ADD COLUMN approval_rate VARCHAR(255);

ALTER TABLE main.suppliers
  ADD COLUMN next_review_date DATE;

ALTER TABLE main.suppliers
  ADD COLUMN review_template_id BIGINT;
ALTER TABLE main.suppliers
  ADD FOREIGN KEY (review_template_id)
    REFERENCES main.review_templates (id) MATCH SIMPLE
    ON DELETE CASCADE;

ALTER TABLE main.suppliers
  ADD COLUMN creator_id BIGINT NOT NULL;
ALTER TABLE main.suppliers
  ADD FOREIGN KEY (creator_id)
    REFERENCES main.employees (id) MATCH SIMPLE
    ON DELETE CASCADE;

ALTER TABLE main.suppliers
  ADD COLUMN review_responsible_id BIGINT NOT NULL;
ALTER TABLE main.suppliers
  ADD FOREIGN KEY (review_responsible_id)
    REFERENCES main.employees (id) MATCH SIMPLE
    ON DELETE CASCADE;

-- add column to supplier_categories
DROP table IF EXISTS main.supplier_categories CASCADE;

CREATE TABLE main.supplier_categories
(
  id          BIGINT       NOT NULL
    CONSTRAINT supplier_categories_pkey
      PRIMARY KEY,
  name_da     varchar(255) NOT NULL
    constraint uk_name_da_supplier_categories
      unique,
  name_en     varchar(255) NOT NULL
    constraint uk_name_en_supplier_categories
      unique,
  description varchar(255)
);

alter table main.supplier_categories
  owner to ledoc;

--create supplier_logs
create table main.supplier_logs
(
  id          bigint                              not null
    constraint supplier_logs_pkey
      primary key,
  created     timestamp default CURRENT_TIMESTAMP not null,
  employee_id bigint                              not null,
  type        varchar(255)                        not null,
  supplier_id bigint                              not null,
  CONSTRAINT employee_logs_fk_1
    FOREIGN KEY (employee_id) REFERENCES main.employees (id),
  CONSTRAINT employee_logs_fk_2
    FOREIGN KEY (supplier_id) REFERENCES main.suppliers (id)
);

alter table main.supplier_logs
  owner to ledoc;

--create supplier_edit_details
create table main.supplier_edit_details
(
  property   varchar(50) not null,
  prev_value varchar,
  cur_value  varchar,
  log_id     bigint      not null
    constraint fk_supplier_edit_details_logs
      references main.supplier_logs
      on delete cascade,
  constraint supplier_edit_details_pkey primary key (property, log_id)
);

ALTER TABLE main.supplier_edit_details
  OWNER TO ledoc;

update main.suppliers
set responsible_id = 1
where main.suppliers.responsible_id is null;
alter table main.suppliers
  alter column responsible_id set not null;



