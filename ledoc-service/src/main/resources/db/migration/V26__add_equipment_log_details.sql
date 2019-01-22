create table main.equipment_edit_details
(
  property   varchar(50) not null,
  prev_value varchar,
  cur_value  varchar,
  log_id     bigint      not null
    constraint fk_equipment_edit_details_logs
      references main.equipment_logs
      on delete cascade,
  constraint equipment_edit_details_pkey primary key (property, log_id)
);

ALTER TABLE main.equipment_edit_details
  OWNER TO ledoc;