 create sequence main.support_ticket_seq
   increment by 50;

 alter sequence main.support_ticket_seq
   owner to ledoc;

create table main.support_tickets
(
  id              bigint                         not null
    constraint support_tickets_pkey
    primary key,
  created timestamp default CURRENT_DATE not null,
  employee_id           bigint                   not null,
  theme           varchar(255)                   not null,
  message         varchar(4000)                  not null,
  page_location       varchar(255)               not null,
  CONSTRAINT support_tickets_fk_1
  FOREIGN KEY (employee_id) REFERENCES main.employees (id)
);

ALTER TABLE main.support_tickets OWNER TO ledoc;