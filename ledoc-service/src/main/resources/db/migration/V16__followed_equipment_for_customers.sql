-- Followed equipment
CREATE TABLE main.followed_equipment
(
  employee_id BIGINT NOT NULL,
  equipment_id BIGINT NOT NULL,
  forced BOOLEAN DEFAULT FALSE NOT NULL,
  PRIMARY KEY (employee_id, equipment_id),
  CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES main.employees (id),
  CONSTRAINT fk_equipment FOREIGN KEY (equipment_id) REFERENCES main.equipment (id)
);

ALTER TABLE main.followed_equipment OWNER TO ledoc;

-- Followed employees
CREATE TABLE main.followed_employees
(
  employee_id BIGINT NOT NULL,
  followed_employee_id BIGINT NOT NULL,
  forced BOOLEAN DEFAULT FALSE NOT NULL,
  PRIMARY KEY (employee_id, followed_employee_id),
  CONSTRAINT fk_employee FOREIGN KEY (employee_id) REFERENCES main.employees (id),
  CONSTRAINT fk_followed_employee FOREIGN KEY (followed_employee_id) REFERENCES main.employees (id)
);

ALTER TABLE main.followed_employees OWNER TO ledoc;

-- Equipment logs
create table main.equipment_logs
(
  id              bigint                         not null
    constraint equipment_logs_pkey
    primary key,
  created timestamp default CURRENT_TIMESTAMP not null,
  employee_id           bigint                   not null,
  type           varchar(255)                   not null,
  equipment_id           bigint                   not null,
  CONSTRAINT employee_logs_fk_1
  FOREIGN KEY (employee_id) REFERENCES main.employees (id),
  CONSTRAINT employee_logs_fk_2
  FOREIGN KEY (equipment_id) REFERENCES main.equipment (id)
);

ALTER TABLE main.equipment_logs OWNER TO ledoc;