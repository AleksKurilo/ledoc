update main.customers set point_of_contact = 101 where point_of_contact is null;
alter table main.customers alter column point_of_contact set not null;

alter table main.employees alter column customer_id drop not null;

update main.employees set password = '185cb748b5e5b09541a0c6c430d06e7a24c343f95ad7fa89797f346f'
where main.employees.id IN (101, 102, 103);