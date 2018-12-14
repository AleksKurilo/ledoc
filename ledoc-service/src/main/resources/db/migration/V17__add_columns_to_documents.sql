
------------ create 'document_categories' table -------------------

create table main.document_categories
(
  id   bigint       not null
    constraint document_categories_pk
      primary key,
  name varchar(255) not null,
  type varchar(25)  not null
);

alter table main.document_categories
  owner to ledoc;

create unique index document_categories_id_uindex
  on main.document_categories (id);
-- auto-generated definition
create sequence document_categories_seq
  increment by 50;

alter sequence document_categories_seq owner to ledoc;

------------------ create new table 'document' --------------

DROP table IF EXISTS main.documents CASCADE;

create table documents
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
  creator_id       bigint                not null
    constraint documents_creator_id_fkey
      references employees,
  constraint documents_name_customer_unique
    unique (name, customer_id),
  constraint documents_check
    check ((equipment_id IS NOT NULL) OR (employee_id IS NOT NULL))
);

alter table documents
  owner to ledoc;

--------------- drop unused tables -------
DROP table IF EXISTS main.employee_log CASCADE;
DROP table IF EXISTS main.equipment_log CASCADE;


------------------ create document_logs -----------
create table main.document_logs
(
  id          bigint                              not null
    constraint document_logs_pkey
      primary key,
  created     timestamp default CURRENT_TIMESTAMP not null,
  employee_id bigint                              not null
    constraint employee_logs_fk_1
      references employees,
  type        varchar(255)                        not null,
  document_id bigint                              not null
    constraint employee_logs_fk_2
      references documents
);

alter table main.document_logs
  owner to ledoc;

