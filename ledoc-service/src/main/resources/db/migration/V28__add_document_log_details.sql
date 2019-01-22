create table main.document_edit_details
(
  property   varchar(50) not null,
  prev_value varchar,
  cur_value  varchar,
  log_id     bigint      not null
    constraint fk_document_edit_details_logs
      references main.document_logs
      on delete cascade,
  constraint document_edit_details_pkey primary key (property, log_id)
);

ALTER TABLE main.document_edit_details
  OWNER TO ledoc;

update main.documents
set responsible_id = 1
where main.documents.responsible_id is null;
alter table main.documents
  alter column responsible_id set not null;