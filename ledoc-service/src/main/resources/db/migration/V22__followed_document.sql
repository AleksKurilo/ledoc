create table main.followed_document
(
  employee_id bigint                not null
    constraint fk_employee
      references employees,
  document_id bigint                not null
    constraint fk_document
      references documents,
  forced      boolean default false not null,
  read        boolean default false not null,
  constraint followed_document_pkey
    primary key (employee_id, document_id)
);

alter table main.followed_document
  owner to ledoc;
