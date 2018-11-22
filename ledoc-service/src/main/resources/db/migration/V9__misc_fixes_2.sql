alter table main.access_tokens
  drop constraint if exists uk_2nbkblqhr9bb6y8u3870rk4hv;

alter table main.locations add type varchar(10);
update main.locations set type = case when address_location_id is null THEN 'ADDRESS' ELSE 'PHYSICAL' END;
alter table main.locations alter type set not null;