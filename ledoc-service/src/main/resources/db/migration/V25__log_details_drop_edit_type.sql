delete from main.employee_edit_details where edit_type != 'VALUE_CHANGED';
alter table main.employee_edit_details drop constraint employee_edit_details_id;
alter table main.employee_edit_details drop column edit_type;
alter table main.employee_edit_details add constraint employee_edit_details_pkey PRIMARY KEY (log_id, property);
