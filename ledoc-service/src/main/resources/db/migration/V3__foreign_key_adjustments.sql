ALTER TABLE main.trade_to_customer DROP CONSTRAINT fk854yxhoaum2uxk93o1gcmc9ia;
ALTER TABLE main.trade_to_customer
  ADD CONSTRAINT fk854yxhoaum2uxk93o1gcmc9ia
FOREIGN KEY (trade_id) REFERENCES main.trades (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE main.equipment_log DROP CONSTRAINT fkqo1gh1o4dsnbodnbkqkxwa02v;
ALTER TABLE main.equipment_log
  ADD CONSTRAINT fkqo1gh1o4dsnbodnbkqkxwa02v
FOREIGN KEY (employee_id) REFERENCES main.employees (id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE main.employee_location DROP CONSTRAINT fkb5phnbjvijodfynsp0us8mine;
ALTER TABLE main.employee_location
  ADD CONSTRAINT fkb5phnbjvijodfynsp0us8mine
FOREIGN KEY (employee_id) REFERENCES main.employees (id);

ALTER TABLE main.employee_location DROP CONSTRAINT fkrnco98d1bidjargw2ut3lae1;
ALTER TABLE main.employee_location
  ADD CONSTRAINT fkrnco98d1bidjargw2ut3lae1
FOREIGN KEY (location_id) REFERENCES main.locations (id) ON DELETE CASCADE ON UPDATE CASCADE;