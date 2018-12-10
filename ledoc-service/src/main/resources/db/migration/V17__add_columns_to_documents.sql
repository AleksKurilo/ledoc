
------------ create 'document_categories' table -------------------

CREATE TABLE main.document_categories
(
  id        bigint                                              NOT NULL,
  name      character varying(255) COLLATE pg_catalog."default" NOT NULL,
  parent_id bigint,
  CONSTRAINT document_categories_pk PRIMARY KEY (id),
  CONSTRAINT parent_id FOREIGN KEY (parent_id)
    REFERENCES main.document_categories (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE CASCADE
)
  WITH (
    OIDS = FALSE
  )
  TABLESPACE pg_default;

ALTER TABLE main.document_categories
  OWNER to ledoc;

CREATE UNIQUE INDEX document_categories_id_uindex
  ON main.document_categories USING btree
    (id)
  TABLESPACE pg_default;

-- auto-generated definition
create sequence document_categories_seq
  increment by 50;

alter sequence document_categories_seq owner to ledoc;

------------------ create new table 'document' --------------

DROP table IF EXISTS main.documents CASCADE;

create table main.documents
(
  id               bigint                not null
    constraint documents_pkey
      primary key,
  file             text                  not null,
  archived         boolean default false not null,
  create_on        date    default now() not null,
  last_update      date,
  employee_id      bigint
    constraint documents_employee_id_fkey
      references employees,
  equipment_id     bigint
    constraint documents_equipment_id_fkey
      references equipment,
  name             varchar(40)           not null,
  customer_id      bigint                not null
    constraint fkey_documents_customer
      references customers,
  archive_reason   varchar(255),
  type             varchar(20),
  source           varchar(20),
  status           varchar(30),
  private          boolean default false not null,
  comment          varchar(255),
  next_review_date date,
  approval_rate    varchar(100),
  category_id      bigint
    constraint documents_category_id_fkey
      references document_categories,
  subcategory_id   bigint
    constraint documents_subcategory_id_fkey
      references document_categories,
  location_id      bigint                not null
    constraint documents_location_id_fkey
      references locations,
  trade_id         bigint                not null
    constraint documents_trade_id_fkey
      references trades,
  responsible_id   bigint
    constraint documents_responsible_id_fkey
      references employees,
  constraint documents_name_customer_unique
    unique (name, customer_id),
  constraint documents_check
    check ((equipment_id IS NOT NULL) OR (employee_id IS NOT NULL))
);

alter table main.documents
  owner to ledoc;
