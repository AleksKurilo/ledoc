create sequence access_tokens_seq
  increment by 50;

alter sequence access_tokens_seq
  owner to ledoc;

create sequence authentication_type_seq
  increment by 50;

alter sequence authentication_type_seq
  owner to ledoc;

create sequence customer_seq
  increment by 50;

alter sequence customer_seq
  owner to ledoc;

create sequence email_notifications_seq
  increment by 50;

alter sequence email_notifications_seq
  owner to ledoc;

create sequence employee_seq
  increment by 50;

alter sequence employee_seq
  owner to ledoc;

create sequence equipment_cat_seq
  increment by 50;

alter sequence equipment_cat_seq
  owner to ledoc;

create sequence equipment_seq
  increment by 50;

alter sequence equipment_seq
  owner to ledoc;

create sequence location_seq
  increment by 50;

alter sequence location_seq
  owner to ledoc;

create sequence supplier_cat_seq
  increment by 50;

alter sequence supplier_cat_seq
  owner to ledoc;

create sequence supplier_seq
  increment by 50;

alter sequence supplier_seq
  owner to ledoc;

create sequence trades_seq
  increment by 50;

alter sequence trades_seq
  owner to ledoc;

create table access_tokens
(
  id              bigint                         not null
    constraint access_tokens_pkey
    primary key,
  expiration_date timestamp default CURRENT_DATE not null,
  state           varchar(255)                   not null,
  token           varchar(255)                   not null
    constraint uk_2nbkblqhr9bb6y8u3870rk4hv
    unique,
  user_id         bigint                         not null,
  new_token       varchar
);

alter table access_tokens
  owner to ledoc;

create table authentication_types
(
  id      bigint       not null
    constraint authentication_types_pkey
    primary key,
  name_da varchar(255),
  name_en varchar(255) not null
    constraint uk_7n1ketan1wbsbmxb0krcsvvyx
    unique
);

alter table authentication_types
  owner to ledoc;

create table customers
(
  id               bigint                    not null
    constraint customers_pkey
    primary key,
  archived         boolean default false     not null,
  company_email    varchar(255),
  contact_email    varchar(255),
  contact_phone    varchar(20),
  cvr              varchar(40)               not null
    constraint uk_ah9k9v16bl2nof6q8i1e3gvfy
    unique,
  date_of_creation date default CURRENT_DATE not null,
  invoice_email    varchar(255),
  mailbox          varchar(255),
  name             varchar(40)               not null
    constraint uk_to73biqfrti0j9f7k12l255w5
    unique,
  point_of_contact bigint
);

alter table customers
  owner to ledoc;

create table email_notifications
(
  id        bigint             not null
    constraint email_notifications_pkey
    primary key,
  email_key varchar(100)       not null,
  model     json               not null,
  recipient varchar(255)       not null,
  retries   smallint default 0 not null,
  status    varchar(255)       not null
);

alter table email_notifications
  owner to ledoc;

create table employees
(
  id                       bigint                not null
    constraint employees_pkey
    primary key,
  archive_reason           varchar(255),
  archived                 boolean default false not null,
  avatar                   bytea,
  cell_phone               varchar(40),
  comment                  varchar(400),
  next_review_date         date,
  review_frequency         varchar(255),
  expire_id_card           date,
  first_name               varchar(255)          not null,
  id_number                varchar(40),
  initials                 varchar(40),
  last_name                varchar(255)          not null,
  rel_comment              varchar(400),
  rel_email                varchar(40),
  rel_first_name           varchar(40),
  rel_last_name            varchar(40),
  rel_phone_number         varchar(40),
  password                 varchar(56)           not null,
  address                  varchar(255),
  building_no              varchar(40),
  city                     varchar(40),
  date_of_birth            date,
  day_of_employment        date,
  personal_mobile          varchar(40),
  personal_phone           varchar(40),
  postal_code              varchar(40),
  private_email            varchar(40),
  phone_number             varchar(40),
  title                    varchar(40),
  username                 varchar(255)          not null,
  personal_comment         varchar(400)
    constraint uk_3gqbimdf7fckjbwt1kcud141m
    unique,
  customer_id              bigint                not null
    constraint fk839aj9u3sefds64rdrukftwsk
      references customers
        on delete cascade,
  responsible_of_skills_id bigint
    constraint fkhfme6mtnarq5j2lakfeamcctp
      references employees,
  place_of_employment_id   bigint,
  responsible_id           bigint
    constraint fkgbr8cd6uc6psahsnn05knpy9s
      references employees
);

alter table employees
  owner to ledoc;

alter table customers
  add constraint fkesk36hgjns3jh8hssi6vcbewj
foreign key (point_of_contact)
  references employees;

create table employee_authorities
(
  employee_id bigint  not null
    constraint fkjjrkjk95qb5n0c2jhay9o3x91
      references employees,
  authority   integer not null,
  constraint employee_authorities_pkey
  primary key (employee_id, authority)
);

alter table employee_authorities
  owner to ledoc;

create table employee_log
(
  visited_id  bigint not null
    constraint fktbgny0u3aquexcops0idk0qni
      references employees
        on delete cascade,
  employee_id bigint not null
    constraint fk6rwhtcbh96i56jur74w8o4kc7
      references employees
        on delete cascade,
  constraint employee_log_pkey
  primary key (visited_id, employee_id)
);

alter table employee_log
  owner to ledoc;

create table equipment_categories
(
  id               bigint       not null
    constraint equipment_categories_pkey
    primary key,
  name_da          varchar(255) not null
    constraint uk_551fpcvrc9sqok21tkckfhdry
    unique,
  name_en          varchar(255) not null
    constraint uk_9bir3k64xdom0f4ft3q32iiwe
    unique,
  review_frequency varchar(255)
);

alter table equipment_categories
  owner to ledoc;

create table locations
(
  id                  bigint                not null
    constraint locations_pkey
    primary key,
  archive_reason      varchar(255),
  archived            boolean default false not null,
  is_cust_first       boolean default false not null,
  name                varchar(40)           not null,
  address_location_id bigint
    constraint fk76aumfppc8rrhh84bnyovjhim
      references locations
        on delete cascade,
  customer_id         bigint                not null
    constraint fkhfst25w1ktr6xlbf5uckh45jl
      references customers
        on delete cascade,
  responsible_id      bigint                not null
    constraint fkmy8utxdwa61gy7c7c4fmiktyt
      references employees
        on delete cascade,
  constraint ukh5cm9dthi0a64fkc7uhc202o9
  unique (name, customer_id)
);

alter table locations
  owner to ledoc;

create table addresses
(
  building_number varchar(40),
  city            varchar(40) not null,
  country         varchar(40) not null,
  district        varchar(40),
  postal_code     varchar(40) not null,
  street          varchar(40) not null,
  location_id     bigint      not null
    constraint addresses_pkey
    primary key
    constraint fk7wf86ejdl44syummkme09y06f
      references locations
        on delete cascade
);

alter table addresses
  owner to ledoc;

create table employee_location
(
  location_id bigint not null
    constraint fkrnco98d1bidjargw2ut3lae1
      references locations,
  employee_id bigint not null
    constraint fkb5phnbjvijodfynsp0us8mine
      references employees
        on delete cascade,
  constraint employee_location_pkey
  primary key (location_id, employee_id)
);

alter table employee_location
  owner to ledoc;

alter table employees
  add constraint fk33wd6wd27qu32d0tia97njvhq
foreign key (place_of_employment_id)
  references locations;

create table reset_tokens
(
  username varchar(255) not null
    constraint reset_tokens_pkey
    primary key,
  token    varchar(36)  not null
    constraint uk_kea6nxkvr7x40yfyyo6ihuy16
    unique
);

alter table reset_tokens
  owner to ledoc;

create table supplier_categories
(
  id          bigint       not null
    constraint supplier_categories_pkey
    primary key,
  description varchar(255),
  name        varchar(255) not null
    constraint uk_r8e092lehlfdwf5o05101lk7s
    unique
);

alter table supplier_categories
  owner to ledoc;

create table suppliers
(
  id             bigint                not null
    constraint suppliers_pkey
    primary key,
  archive_reason varchar(255),
  archived       boolean default false not null,
  contact_email  varchar(255),
  contact_phone  varchar(255),
  description    varchar(255),
  name           varchar(255)          not null,
  category_id    bigint                not null
    constraint fk6bsqixwfkgkgqt6j3dmoq245
      references supplier_categories
        on delete cascade,
  customer_id    bigint                not null
    constraint fkm6agja9dsr4d2hcie2hsunoeo
      references customers
        on delete cascade,
  responsible_id bigint                not null
    constraint fkd3kenrlo8kpfj6bd1vcnledq9
      references employees,
  constraint uk2vb3wrvt6r8y9jnn90pe35aln
  unique (name, customer_id)
);

alter table suppliers
  owner to ledoc;

create table trades
(
  id      bigint       not null
    constraint trades_pkey
    primary key,
  name_da varchar(255) not null,
  name_en varchar(255) not null
);

alter table trades
  owner to ledoc;

create table trade_to_customer
(
  customer_id bigint not null
    constraint fk4gfjq8enoogwytkt9rf76qmtr
      references customers,
  trade_id    bigint not null
    constraint fk854yxhoaum2uxk93o1gcmc9ia
      references trades,
  constraint trade_to_customer_pkey
  primary key (customer_id, trade_id)
);

alter table trade_to_customer
  owner to ledoc;

create table equipment
(
  id               bigint                                        not null
    constraint equipment_pkey
    primary key,
  archive_reason   varchar(255),
  archived         boolean default false                         not null,
  remark           varchar(255),
  id_number        varchar(255),
  local_id         varchar(255),
  manufacturer     varchar(255),
  name             varchar(255)                                  not null,
  price            numeric(19, 2),
  purchase_date    date                                          not null,
  serial_number    varchar(255),
  status           varchar(10) default 'OK' :: character varying not null,
  category_id      bigint                                        not null
    constraint fkh361axu3qx315uubmflrkpn7w
      references equipment_categories
        on delete cascade,
  creator_id       bigint                                        not null
    constraint fk5fkr355w38h00a1kutmn1l7lm
      references employees,
  customer_id      bigint                                        not null
    constraint fkpc406o0ir8f0fjym2gt0f6j4
      references customers
        on delete cascade,
  supplier_id      bigint
    constraint fkmt7irxfwy9665fjppv6qtndbd
      references suppliers,
  responsible_id   bigint                                        not null
    constraint fk_equipment_responsible
      references employees,
  warranty_date    date,
  location_id      bigint                                        not null
    constraint fknx2yctt0ccxwxejow1xda20yb
      references locations,
  auth_type_id     bigint
    constraint fk7murgieuyng08cgb29ulnyw14
      references authentication_types,
  approval_rate    varchar(255),
  approval_type    varchar(255)                                  not null,
  avatar           bytea,
  next_review_date date,
  constraint ukfrqq1seeoxc0rtcht97i3qk60
  unique (name, customer_id)
);

alter table equipment
  owner to ledoc;

create table equipment_loans
(
  borrower_responsible_for_review boolean default false not null,
  comment                         varchar(255),
  deadline                        date,
  should_be_inspected             boolean default true  not null,
  equipment_id                    bigint                not null
    constraint equipment_loans_pkey
    primary key
    constraint fk7p23srf2od30pmy4b2n5i22ng
      references equipment
        on delete cascade,
  borrower_id                     bigint                not null
    constraint fkoauqkf32x4d28rpa50ubqpp2a
      references employees,
  location_id                     bigint                not null
    constraint fkej3qbf0uhtjbs3w2nhc1ke9iv
      references locations
);

alter table equipment_loans
  owner to ledoc;

create table equipment_log
(
  equipment_id bigint not null
    constraint fkj83t7dwx4e0ks69wxsgr56ap0
      references equipment
        on delete cascade,
  employee_id  bigint not null
    constraint fkqo1gh1o4dsnbodnbkqkxwa02v
      references employees,
  constraint equipment_log_pkey
  primary key (equipment_id, employee_id)
);

alter table equipment_log
  owner to ledoc;

CREATE OR REPLACE VIEW main.customers_export_excel AS
  SELECT customers.id AS customer_id,
         customers.name,
         customers.cvr,
         customers.date_of_creation,
         count(sup.id) AS active_suppliers,
         count(sup1.id) AS all_suppliers,
         count(em.id) AS active_empl,
         count(em1.id) AS all_empl,
         count(eq.id) AS active_equipments,
         count(eq1.id) AS all_equipments,
         count(loc.id) AS locations,
         customers.contact_phone,
         customers.company_email,
         addr.city,
         addr.postal_code,
         addr.street,
         addr.building_number,
         addr.district,
         (em.username::text || ' - '::text) || em.first_name::text AS point_of_contact
  FROM main.customers
         LEFT JOIN main.employees em ON customers.id = em.customer_id AND em.archived IS FALSE
         LEFT JOIN main.employees em1 ON customers.id = em1.customer_id
         LEFT JOIN main.locations loc ON customers.id = loc.customer_id AND loc.is_cust_first IS TRUE
         LEFT JOIN main.suppliers sup ON customers.id = sup.id AND sup.archived IS FALSE
         LEFT JOIN main.suppliers sup1 ON customers.id = sup1.id
         LEFT JOIN main.equipment eq ON customers.id = eq.id AND eq.archived IS FALSE
         LEFT JOIN main.equipment eq1 ON customers.id = eq1.id
         LEFT JOIN main.addresses addr ON loc.id = addr.location_id
  GROUP BY customers.id, customers.name, customers.cvr, customers.date_of_creation, customers.company_email, customers.contact_phone, addr.city, addr.postal_code, addr.street, addr.building_number, addr.district, em.username, em.first_name
  ORDER BY customers.id;

ALTER TABLE main.customers_export_excel
  OWNER TO ledoc;

