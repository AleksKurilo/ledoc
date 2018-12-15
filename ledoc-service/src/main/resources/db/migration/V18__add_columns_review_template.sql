ALTER TABLE main.documents
  ADD COLUMN review_template_id bigint;

ALTER TABLE main.documents
  ADD FOREIGN KEY (review_template_id)
    REFERENCES main.review_templates (id) MATCH SIMPLE
    on delete cascade;