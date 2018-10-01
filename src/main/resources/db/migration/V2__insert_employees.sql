--create super_admin
INSERT INTO "main"."customers" ("id", "archived", "company_email", "contact_email", "contact_phone", "cvr", "date_of_creation", "invoice_email", "mailbox", "name", "point_of_contact")
VALUES (1, DEFAULT, NULL, NULL, NULL, '123', DEFAULT, NULL, NULL, 'super_admin', NULL);
INSERT INTO "main"."employees" ("id", "archive_reason", "archived", "avatar", "cell_phone", "comment", "next_review_date", "review_frequency", "expire_id_card", "first_name", "id_number", "initials", "last_name", "rel_comment", "rel_email", "rel_first_name", "rel_last_name", "rel_phone_number", "password", "address", "building_no", "city", "date_of_birth", "day_of_employment", "personal_mobile", "personal_phone", "postal_code", "private_email", "phone_number", "title", "username", "customer_id", "responsible_of_skills_id", "place_of_employment_id", "responsible_id")
VALUES (1, 'NULL', DEFAULT, NULL, NULL, NULL, NULL, NULL, NULL, 'super', 'NULL', NULL, 'super_admin', NULL, NULL, NULL, NULL, NULL, 'd7b36026513208b1933f64ac6310a2c92cfcea59cc09752f8b4d0649', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'super_admin@gmail.com', 1, NULL, NULL, NULL);
INSERT INTO "main"."employee_authorities" ("employee_id", "authority") VALUES (1, 2);

--create admin
INSERT INTO "main"."customers" ("id", "archived", "company_email", "contact_email", "contact_phone", "cvr", "date_of_creation", "invoice_email", "mailbox", "name", "point_of_contact") VALUES (2, DEFAULT, NULL, NULL, NULL, '1234', DEFAULT, NULL, NULL, 'admin', NULL);

INSERT INTO "main"."employees" ("id", "archive_reason", "archived", "avatar", "cell_phone", "comment", "next_review_date", "review_frequency", "expire_id_card", "first_name", "id_number", "initials", "last_name", "rel_comment", "rel_email", "rel_first_name", "rel_last_name", "rel_phone_number", "password", "address", "building_no", "city", "date_of_birth", "day_of_employment", "personal_mobile", "personal_phone", "postal_code", "private_email", "phone_number", "title", "username", "customer_id", "responsible_of_skills_id", "place_of_employment_id", "responsible_id") VALUES (2, NULL, DEFAULT, NULL, NULL, NULL, NULL, NULL, NULL, 'admin', NULL, NULL, 'admin', NULL, NULL, NULL, NULL, NULL, 'd7b36026513208b1933f64ac6310a2c92cfcea59cc09752f8b4d0649', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'admin@gmail.com', 2, NULL, NULL, NULL);

INSERT INTO "main"."employee_authorities" ("employee_id", "authority") VALUES (2, 1);

--employee
INSERT INTO "main"."customers" ("id", "archived", "company_email", "contact_email", "contact_phone", "cvr", "date_of_creation", "invoice_email", "mailbox", "name", "point_of_contact") VALUES (3, DEFAULT, 'NULL', 'NULL', 'NULL', '12345', DEFAULT, 'NULL', 'NULL', 'employee', NULL);

INSERT INTO "main"."employees" ("id", "archive_reason", "archived", "avatar", "cell_phone", "comment", "next_review_date", "review_frequency", "expire_id_card", "first_name", "id_number", "initials", "last_name", "rel_comment", "rel_email", "rel_first_name", "rel_last_name", "rel_phone_number", "password", "address", "building_no", "city", "date_of_birth", "day_of_employment", "personal_mobile", "personal_phone", "postal_code", "private_email", "phone_number", "title", "username", "customer_id", "responsible_of_skills_id", "place_of_employment_id", "responsible_id") VALUES (3, NULL, DEFAULT, NULL, NULL, NULL, NULL, NULL, NULL, 'employee', NULL, NULL, 'employee', NULL, NULL, NULL, NULL, NULL, 'd7b36026513208b1933f64ac6310a2c92cfcea59cc09752f8b4d0649', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'employee@gmail.com', 3, NULL, NULL, NULL);

INSERT INTO "main"."employee_authorities" ("employee_id", "authority") VALUES (3, 0);