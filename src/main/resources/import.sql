drop function if exists main.set_user_authorities_fn();

create function main.set_user_authorities_fn() returns trigger language plpgsql as $$ BEGIN  /* "0" is the code of "user" authority in UserAuthorities class */ INSERT INTO main.employees_authorities VALUES (NEW.id, 0); RETURN NEW;END;$$;

create trigger set_default_authorities after insert on main.employees for each row execute procedure main.set_user_authorities_fn();