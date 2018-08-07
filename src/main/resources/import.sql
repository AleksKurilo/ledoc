drop function if exists main.set_user_authorities_fn();

create function main.set_user_authorities_fn() returns trigger language plpgsql as $$ BEGIN  /* "0" is the code of "user" authority in UserAuthorities class */ INSERT INTO main.employee_authorities VALUES (NEW.id, 0); RETURN NEW;END;$$;

create trigger set_default_authorities after insert on main.employees for each row execute procedure main.set_user_authorities_fn();

insert into main.customers (id, cvr, name) VALUES (1, 'cvr', 'name');

insert into main.employees (id, username, password, first_name, last_name, title, customer_id) VALUES (1, 'testmytest38@gmail.com', '7e182d1a0935b721929a24616155405f81599a267b7a3ccffe384327', 'fn', 'ln', 'title', 1);
insert into main.employees (id, username, password, first_name, last_name, title, customer_id) VALUES (2, 'ledocapp@gmail.com', '7e182d1a0935b721929a24616155405f81599a267b7a3ccffe384327', 'fn', 'ln', 'title', 1);

insert into main.employee_authorities VALUES (1, 1);
insert into main.employee_authorities VALUES (2, 1);
insert into main.employee_authorities VALUES (1, 2);
insert into main.employee_authorities VALUES (2, 2);