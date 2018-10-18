------------------------- review_templates -------------------

create table main.review_templates (
  module varchar(31) not null,
  id int8 not null,
  archived boolean default false not null,
  editable boolean default true not null,
  is_global boolean default false not null,
  name varchar(100) not null,
  customer_id int8,
  primary key (id)
);

alter table main.review_templates owner to ledoc;

create sequence main.review_template_seq start 1 increment 50;

alter sequence main.review_template_seq owner to ledoc;

alter table if exists main.review_templates
  add constraint FK9grt5kx2r3t42ef1uu0f1a3si
foreign key (customer_id)
references main.customers
on delete cascade;

alter table if exists main.review_templates
  add constraint UKmj8r5qktmbt1rdqbls0pubpr unique (name, customer_id);

------------------------- equipment_review_template_categories -------------------

create table main.equipment_review_template_categories (
  review_template_id int8 not null,
  category_id int8 not null,
  primary key (review_template_id, category_id)
);

alter table if exists main.equipment_review_template_categories
  add constraint FKr15hxuoegpiaw517m78fsc8mk
foreign key (category_id)
references main.equipment_categories
on delete cascade;

alter table if exists main.equipment_review_template_categories
  add constraint FKt7906fi19bdx4tdto4cxe2isb
foreign key (review_template_id)
references main.review_templates
on delete cascade;

------------------------- question_groups -------------------

create table main.question_groups (
  id int8 not null,
  content varchar(500),
  name varchar(100) not null,
  customer_id int8,
  primary key (id)
);

alter table main.question_groups owner to ledoc;

create sequence main.question_group_seq start 1 increment 50;

alter sequence main.question_group_seq owner to ledoc;

alter table if exists main.question_groups
  add constraint FKn6qs0rlk8nfh1ty21csnvumck
foreign key (customer_id)
references main.customers
on delete cascade;

------------------------- review_templates_question_groups -------------------

create table main.review_templates_question_groups (
  review_template_id int8 not null,
  question_group_id int8 not null,
  primary key (review_template_id, question_group_id)
);

alter table if exists main.review_templates_question_groups
  add constraint FKhg1dhmgfm8tko4uto1t31jbaf
foreign key (question_group_id)
references main.question_groups
on delete cascade;

alter table if exists main.review_templates_question_groups
  add constraint FKj56qqltshi5em4i2ne1ugo1bf
foreign key (review_template_id)
references main.review_templates
on delete cascade;

------------------------- review_questions -------------------

create table main.review_questions (
  id int8 not null,
  question_type varchar(255) not null,
  title varchar(100) not null,
  wording varchar(4000) not null,
  customer_id int8,
  primary key (id)
);

alter table main.review_questions owner to ledoc;

create sequence main.review_question_seq start 1 increment 50;

alter sequence main.review_question_seq owner to ledoc;

alter table if exists main.review_questions
  add constraint FKklq6j59agdcsx2k6ovhv226p5
foreign key (customer_id)
references main.customers
on delete cascade;

------------------------- question_groups_review_questions -------------------

create table main.question_groups_review_questions (
  question_group_id int8 not null,
  review_question_id int8 not null,
  primary key (question_group_id, review_question_id)
);

alter table if exists main.question_groups_review_questions
  add constraint FKshi86i4tnpfq6nhvadwgd3iak
foreign key (review_question_id)
references main.review_questions
on delete cascade;

alter table if exists main.question_groups_review_questions
  add constraint FK6dk0085w80fbe47fllyb46ytd
foreign key (question_group_id)
references main.question_groups
on delete cascade;

------------------------- employee_reviews -------------------

create table main.employee_reviews (
  id int8 not null,
  subject_id int8 not null,
  review_template_id int8 not null,
  reviewer_id int8,
  primary key (id)
);

alter table main.employee_reviews owner to ledoc;

create sequence main.employee_review_seq start 1 increment 50;

alter sequence main.employee_review_seq owner to ledoc;

alter table if exists main.employee_reviews
  add constraint FK7jiemmvnb2i0xu0vhfx4b9wr4
foreign key (subject_id)
references main.employees
on delete cascade;

alter table if exists main.employee_reviews
  add constraint FK9txc6716qsaj7ivbhgucuc9uy
foreign key (review_template_id)
references main.review_templates;

alter table if exists main.employee_reviews
  add constraint FK5uga1kjrt8sqa29toqtw05hcv
foreign key (reviewer_id)
references main.employees;

------------------------- employee_review_question_answers -------------------

create table main.employee_review_question_answers (
  id int8 not null,
  answer varchar(1000) not null,
  comment varchar(500),
  review_id int8 not null,
  review_question_id int8 not null,
  primary key (id)
);

alter table main.employee_review_question_answers owner to ledoc;

create sequence main.employee_review_question_answer_seq start 1 increment 50;

alter sequence main.employee_review_question_answer_seq owner to ledoc;

alter table if exists main.employee_review_question_answers
  add constraint FKlwrwdm7ht8nbrs1e2qi87bhet
foreign key (review_id)
references main.employee_reviews
on delete cascade;

alter table if exists main.employee_review_question_answers
  add constraint FKh7jaxpk8fd3qrwhxvq4gp67o9
foreign key (review_question_id)
references main.review_questions;

------------------------- equipment_reviews -------------------

create table main.equipment_reviews (
  id int8 not null,
  subject_id int8 not null,
  review_template_id int8 not null,
  reviewer_id int8,
  primary key (id)
);

alter table main.equipment_reviews owner to ledoc;

create sequence main.equipment_review_seq start 1 increment 50;

alter sequence main.equipment_review_seq owner to ledoc;

alter table if exists main.equipment_reviews
  add constraint FK7jiemmvnb2i0xu0vhfx4b9wr4
foreign key (subject_id)
references main.equipment
on delete cascade;

alter table if exists main.equipment_reviews
  add constraint FK9txc6716qsaj7ivbhgucuc9uy
foreign key (review_template_id)
references main.review_templates;

alter table if exists main.equipment_reviews
  add constraint FK5uga1kjrt8sqa29toqtw05hcv
foreign key (reviewer_id)
references main.employees;

------------------------- equipment_review_question_answers -------------------

create table main.equipment_review_question_answers (
  id int8 not null,
  answer varchar(1000) not null,
  comment varchar(500),
  review_id int8 not null,
  review_question_id int8 not null,
  primary key (id)
);

alter table main.equipment_review_question_answers owner to ledoc;

create sequence main.equipment_review_question_answer_seq start 1 increment 50;

alter sequence main.equipment_review_question_answer_seq owner to ledoc;

alter table if exists main.equipment_review_question_answers
  add constraint FKlwrwdm7ht8nbrs1e2qi87bhet
foreign key (review_id)
references main.equipment_reviews
on delete cascade;

alter table if exists main.equipment_review_question_answers
  add constraint FKh7jaxpk8fd3qrwhxvq4gp67o9
foreign key (review_question_id)
references main.review_questions;

------------------------- document_reviews -------------------

create table main.document_reviews (
  id int8 not null,
  subject_id int8 not null,
  review_template_id int8 not null,
  reviewer_id int8,
  primary key (id)
);

alter table main.document_reviews owner to ledoc;

create sequence main.document_review_seq start 1 increment 50;

alter sequence main.document_review_seq owner to ledoc;

alter table if exists main.document_reviews
  add constraint FK7jiemmvnb2i0xu0vhfx4b9wr4
foreign key (subject_id)
references main.documents
on delete cascade;

alter table if exists main.document_reviews
  add constraint FK9txc6716qsaj7ivbhgucuc9uy
foreign key (review_template_id)
references main.review_templates;

alter table if exists main.document_reviews
  add constraint FK5uga1kjrt8sqa29toqtw05hcv
foreign key (reviewer_id)
references main.employees;

------------------------- document_review_question_answers -------------------

create table main.document_review_question_answers (
  id int8 not null,
  answer varchar(1000) not null,
  comment varchar(500),
  review_id int8 not null,
  review_question_id int8 not null,
  primary key (id)
);

alter table main.document_review_question_answers owner to ledoc;

create sequence main.document_review_question_answer_seq start 1 increment 50;

alter sequence main.document_review_question_answer_seq owner to ledoc;

alter table if exists main.document_review_question_answers
  add constraint FKlwrwdm7ht8nbrs1e2qi87bhet
foreign key (review_id)
references main.document_reviews
on delete cascade;

alter table if exists main.document_review_question_answers
  add constraint FKh7jaxpk8fd3qrwhxvq4gp67o9
foreign key (review_question_id)
references main.review_questions;

------------------------- supplier_reviews -------------------

create table main.supplier_reviews (
  id int8 not null,
  subject_id int8 not null,
  review_template_id int8 not null,
  reviewer_id int8,
  primary key (id)
);

alter table main.supplier_reviews owner to ledoc;

create sequence main.supplier_review_seq start 1 increment 50;

alter sequence main.supplier_review_seq owner to ledoc;

alter table if exists main.supplier_reviews
  add constraint FK7jiemmvnb2i0xu0vhfx4b9wr4
foreign key (subject_id)
references main.suppliers
on delete cascade;

alter table if exists main.supplier_reviews
  add constraint FK9txc6716qsaj7ivbhgucuc9uy
foreign key (review_template_id)
references main.review_templates;

alter table if exists main.supplier_reviews
  add constraint FK5uga1kjrt8sqa29toqtw05hcv
foreign key (reviewer_id)
references main.employees;

------------------------- supplier_review_question_answers -------------------

create table main.supplier_review_question_answers (
  id int8 not null,
  answer varchar(1000) not null,
  comment varchar(500),
  review_id int8 not null,
  review_question_id int8 not null,
  primary key (id)
);

alter table main.supplier_review_question_answers owner to ledoc;

create sequence main.supplier_review_question_answer_seq start 1 increment 50;

alter sequence main.supplier_review_question_answer_seq owner to ledoc;

alter table if exists main.supplier_review_question_answers
  add constraint FKlwrwdm7ht8nbrs1e2qi87bhet
foreign key (review_id)
references main.supplier_reviews
on delete cascade;

alter table if exists main.supplier_review_question_answers
  add constraint FKh7jaxpk8fd3qrwhxvq4gp67o9
foreign key (review_question_id)
references main.review_questions;

------------------------- employees -------------------

alter table main.employees add review_template_id int8;

alter table if exists main.employees
  add constraint FKes8xgud347jl9ubmnhkh0jsx
foreign key (review_template_id)
references main.review_templates;

alter table main.equipment add review_template_id int8;

alter table if exists main.equipment
  add constraint FKphiurphy24x46xq3ccyqljnjp
foreign key (review_template_id)
references main.review_templates;