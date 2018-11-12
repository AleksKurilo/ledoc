 create sequence main.log_seq
   increment by 50;

 alter sequence main.log_seq
   owner to ledoc;

create table main.employee_logs
(
  id              bigint                         not null
    constraint employee_logs_pkey
    primary key,
  created timestamp default CURRENT_TIMESTAMP not null,
  employee_id           bigint                   not null,
  type           varchar(255)                   not null,
  target_employee_id           bigint                   not null,
  CONSTRAINT employee_logs_fk_1
  FOREIGN KEY (employee_id) REFERENCES main.employees (id),
  CONSTRAINT employee_logs_fk_2
  FOREIGN KEY (target_employee_id) REFERENCES main.employees (id)
);

ALTER TABLE main.employee_logs OWNER TO ledoc;