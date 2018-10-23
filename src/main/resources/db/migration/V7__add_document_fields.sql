alter table main.documents add column name varchar(40);
DO $$
DECLARE
  counter INTEGER := 0;
    doc_cursor CURSOR FOR SELECT * FROM main.documents;
  doc RECORD;
begin
  OPEN doc_cursor;
  LOOP
    FETCH doc_cursor INTO doc;
    EXIT WHEN NOT FOUND;

    UPDATE main.documents SET name = '' || counter WHERE CURRENT OF doc_cursor;
    counter := counter + 1;
  END LOOP;
  CLOSE doc_cursor;
END $$;
alter table main.documents alter column name set not null;

alter table main.documents add column customer_id bigint;
update main.documents set customer_id = 101;
alter table main.documents alter column customer_id set not null;

alter table main.documents add column archive_reason varchar(255);

alter table main.documents
  add constraint documents_name_customer_unique
unique (name, customer_id);

alter table main.documents add constraint fkey_documents_customer
foreign key (customer_id) references customers(id);

