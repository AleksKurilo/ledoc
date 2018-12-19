alter table main.documents drop constraint documents_check;
ALTER TABLE main.documents drop column employee_id cascade;
ALTER TABLE main.documents drop column equipment_id cascade;