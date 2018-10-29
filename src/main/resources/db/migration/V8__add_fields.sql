alter table main.locations add creation_date date not null default CURRENT_DATE;

alter table main.addresses add address_type varchar(20);
update main.addresses set address_type = 'DEPARTMENT';
alter table main.addresses alter address_type set not null;

alter table main.locations add created_by bigint;
update main.locations set created_by = 101;
alter table main.locations alter created_by set not null;

alter table main.locations alter responsible_id drop not null;
ALTER TABLE main.locations DROP CONSTRAINT fkmy8utxdwa61gy7c7c4fmiktyt;
ALTER TABLE main.locations
  ADD CONSTRAINT fkmy8utxdwa61gy7c7c4fmiktyt
FOREIGN KEY (responsible_id) REFERENCES main.employees (id) ON DELETE SET NULL ON UPDATE CASCADE;