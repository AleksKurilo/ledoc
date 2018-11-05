alter table main.equipment rename column remark to comment;

alter table main.equipment add ready_to_loan boolean not null default false;