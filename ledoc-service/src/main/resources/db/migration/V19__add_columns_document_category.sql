ALTER TABLE main.document_categories
  RENAME name TO name_en;

ALTER TABLE main.document_categories
  ADD COLUMN name_da varchar(255);


ALTER TABLE main.locations
  ADD COLUMN document_id bigint;

ALTER TABLE main.documents
  DROP COLUMN location_id;
ALTER TABLE main.documents
  ALTER COLUMN create_on DROP NOT NULL;

create table main.document_location
(
  location_id bigint not null
    constraint fkrnco98d1bidjargw2ut3lae1
      references locations
      on update cascade on delete cascade,
  document_id bigint not null
    constraint fkb5phnbjvijodfynsp0us8mine
      references documents,
  constraint document_location_pkey
    primary key (location_id, document_id)
);

alter table main.employee_location
  owner to ledoc;


ALTER TABLE main.documents
  DROP COLUMN trade_id;

create table main.document_trade
(
  trade_id    bigint not null
    constraint fkrnco98d1bidjargw2ut3lae1
      references trades
      on update cascade on delete cascade,
  document_id bigint not null
    constraint fkb5phnbjvijodfynsp0us8mine
      references documents,
  constraint document_trade_pkey
    primary key (trade_id, document_id)
);

alter table main.employee_location
  owner to ledoc;