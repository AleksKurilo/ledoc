package dk.ledocsystem.service.impl.excel.model.equipment;

public class MyEquipmentSheet extends AbstractEquipmentSheet {

    private static final String QUERY = "select main.equipment.name,\n" +
            "       concat(main.equipment_categories.name_en, ' - ', main.equipment_categories.name_da) as category,\n" +
            "       main.equipment.id_number,\n" +
            "       main.equipment.serial_number,\n" +
            "       main.locations.name as homeLocation,\n" +
            "       concat(l.name, ' ') as currentLocation,\n" +
            "       concat(empl.first_name, ' ', empl.last_name) as responsibleReview,\n" +
            "       CASE when eloans.equipment_id NOTNULL then 'Loaned' ELSE 'Not loaned' end as loanStatus,\n" +
            "       status,\n" +
            "       main.equipment.next_review_date as dueDATE,\n" +
            "       main.suppliers.name as supplier,\n" +
            "       '<not_implemented>' as reviewStatus,\n" +
            "       '<not_implemented>' as must_be_reviewed,\n" +
            "       concat(main.authentication_types.name_en, ' - ', main.authentication_types.name_da) as authenticationType,\n" +
            "       concat(main.employees.first_name, ' ', main.employees.last_name) as responsible,\n" +
            "       local_id\n" +
            "from main.equipment\n" +
            "  left join main.equipment_categories ON main.equipment.category_id=main.equipment_categories.id --category\n" +
            "\n" +
            "  LEFT JOIN main.locations on main.equipment.location_id = main.locations.id --home location\n" +
            "\n" +
            "  left join main.equipment_loans on (main.equipment_loans.equipment_id=main.equipment.id)\n" +
            "\n" +
            "  left join main.equipment_loans el on (el.equipment_id=main.equipment.id) -- current location\n" +
            "  LEFT JOIN main.locations l on (main.equipment_loans.location_id = l.id) -- current location\n" +
            "\n" +
            "  left join main.equipment_loans eql on (eql.equipment_id=main.equipment.id and eql.borrower_responsible_for_review is true) -- responsible review\n" +
            "  left join main.employees empl on (eql.borrower_id=empl.id) -- responsible review\n" +
            "\n" +
            "  left JOIN main.equipment_loans eloans on (eloans.equipment_id=main.equipment.id) --loan status\n" +
            "\n" +
            "  left join main.suppliers on main.equipment.supplier_id = main.suppliers.id -- supplier\n" +
            "  left join main.authentication_types on main.equipment.auth_type_id = main.authentication_types.id -- auth type\n" +
            "  left join main.employees on main.equipment.responsible_id = main.employees.id\n" +
            "\n" +
            "WHERE main.equipment.archived is FALSE and equipment.creator_id=?\n" +
            "GROUP BY equipment_categories.name_en,\n" +
            "         equipment_categories.name_da,\n" +
            "         locations.name,\n" +
            "         equipment.id,\n" +
            "         l.name,\n" +
            "         empl.last_name,\n" +
            "         empl.first_name,\n" +
            "         eloans.equipment_id,\n" +
            "         suppliers.name,\n" +
            "         authentication_types.name_en,\n" +
            "         authentication_types.name_da,\n" +
            "         employees.first_name,\n" +
            "         employees.last_name;";

    @Override
    public String getQuery() {
        return QUERY;
    }

    @Override
    public String getName() {
        return "My equipment";
    }

    @Override
    public Object[] getParams() {
        //return new Object[]{((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()};
        return new Object[]{};
    }
}
