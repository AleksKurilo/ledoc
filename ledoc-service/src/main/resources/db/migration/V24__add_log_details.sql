create table main.employee_edit_details
(
  property   varchar(50) not null,
  prev_value varchar,
  cur_value  varchar,
  edit_type  varchar(15) not null,
  log_id     bigint      not null
    constraint fk_employee_edit_details_logs
      references main.employee_logs
      on delete cascade,
  constraint employee_edit_details_id primary key (property, edit_type, log_id)
);

ALTER TABLE main.employee_edit_details
  OWNER TO ledoc;