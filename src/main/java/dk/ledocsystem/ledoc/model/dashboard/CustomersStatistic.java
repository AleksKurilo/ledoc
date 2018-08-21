package dk.ledocsystem.ledoc.model.dashboard;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "customers_export_excel")
public class CustomersStatistic {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "name")
    private String customerName;

    @Column
    private String cvr;

    @Column(name = "date_of_creation")
    private LocalDate dateOfCreation;

    @Column(name = "active_suppliers")
    private int activeSuppliers; //not archived

    @Column(name = "all_suppliers")
    private int allSuppliers; // all

    @Column(name = "active_empl")
    private int activeEmployees; // not archived

    @Column(name = "all_empl")
    private int allEmployees; //all

    /*private int activeDocuments;
    private int allDocuments;*/

    @Column(name = "active_equipments")
    private int activeEquipments;

    @Column(name = "all_equipments")
    private int allEquipments;

    /*private int reviewTemplates;
    private int employeeReviewTemplates;
    private int activeImprovements;
    private int allImprovements;*/

    @Column
    private int locations;

    /*private int logins;
    private LocalDate lastLogging;*/

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "contact_phone")
    private String phoneNumber;

    @Column(name = "company_email")
    private String companyEmail;

    @Column
    private String city;

    @Column
    private String street;

    @Column(name = "building_number")
    private String buildingNumber;

    @Column
    private String district;

    @Column(name = "point_of_contact")
    private String pointOfContact;
}
