DROP VIEW main.customers_export_excel;
CREATE OR REPLACE VIEW main.customers_export_excel AS
SELECT customers.name,
       customers.cvr,
       customers.date_of_creation,
       count(sup.id) AS active_suppliers,
       count(sup1.id) AS all_suppliers,
       count(em.id) AS active_empl,
       count(em1.id) AS all_empl,
       count(eq.id) AS active_equipments,
       count(eq1.id) AS all_equipments,
       count(loc.id) AS locations,
       customers.contact_phone,
       customers.company_email,
       addr.postal_code,
       addr.city,
       addr.street,
       addr.building_number,
       addr.district,
       (em.username::text || ' - '::text) || em.first_name::text AS point_of_contact
FROM main.customers
       LEFT JOIN main.employees em ON customers.id = em.customer_id AND em.archived IS FALSE
       LEFT JOIN main.employees em1 ON customers.id = em1.customer_id
       LEFT JOIN main.locations loc ON customers.id = loc.customer_id AND loc.is_cust_first IS TRUE
       LEFT JOIN main.suppliers sup ON customers.id = sup.id AND sup.archived IS FALSE
       LEFT JOIN main.suppliers sup1 ON customers.id = sup1.id
       LEFT JOIN main.equipment eq ON customers.id = eq.id AND eq.archived IS FALSE
       LEFT JOIN main.equipment eq1 ON customers.id = eq1.id
       LEFT JOIN main.addresses addr ON loc.id = addr.location_id
GROUP BY customers.id, customers.name, customers.cvr, customers.date_of_creation, customers.company_email, customers.contact_phone, addr.city, addr.postal_code, addr.street, addr.building_number, addr.district, em.username, em.first_name
ORDER BY customers.id;

ALTER TABLE main.customers_export_excel
  OWNER TO ledoc;