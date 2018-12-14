package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.QDocument;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.data.model.dashboard.UserStat;
import dk.ledocsystem.data.model.employee.QEmployee;
import dk.ledocsystem.data.model.equipment.QEquipment;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.*;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import dk.ledocsystem.service.impl.excel.model.documents.DocumentsEntitySheet;
import dk.ledocsystem.service.impl.excel.model.employees.EmployeesEntitySheet;
import dk.ledocsystem.service.impl.excel.model.equipment.EquipmentEntitySheet;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private static final Function<Boolean, Predicate> EMPLOYEES_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QEmployee.employee.archived, archived);

    private static final Function<Boolean, Predicate> EQUIPMENT_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QEquipment.equipment.archived, archived);

    private static final Function<Boolean, Predicate> DOCUMENTS_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QDocument.document.archived, archived);

    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final DocumentService documentService;
    private final LocationService locationService;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final JwtTokenService tokenService;
    private final ExcelExportService excelExportService;

    @Override
    public Dashboard createDashboard(UserDetails currentUserDetails) {
        Dashboard dashboard = new Dashboard();
        GetEmployeeDTO currentUser = employeeService.getByUsername(currentUserDetails.getUsername())
                .orElseThrow(IllegalStateException::new);

        dashboard.setNewEmployeesCount(employeeService.getNewEmployees(currentUserDetails, Pageable.unpaged()).getTotalElements());
        dashboard.setNewEquipmentCount(equipmentService.getNewEquipment(currentUserDetails, Pageable.unpaged()).getTotalElements());
        dashboard.setNewLocationsCount(locationService.getAllByCustomer(currentUser.getCustomerId()).size());

        return dashboard;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SuperAdminStatistic createStatistic() {
        SuperAdminStatistic statistic = new SuperAdminStatistic();
        statistic.setUsersOnline(tokenService.countUsersOnline());
        statistic.setTotalActiveCustomersCount(customerRepository.countAllByArchivedFalse());
        UserStat userStat = new UserStat();
        userStat.setTotalActiveAdminsCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.ADMIN));
        userStat.setTotalActiveEmployeesCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.USER));
        statistic.setUserStat(userStat);
        return statistic;
    }

    @Override
    public Workbook exportExcelCustomers() {
        return excelExportService.exportSheet(new CustomersSheet());
    }

//    @Override
//    public Workbook exportExcelEmployees() {
//        return excelExportService.exportSheet(new EmployeesSheet());
//    }

    @Override
    public Workbook exportExcelEmployees(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> employeesSheets = new ArrayList<>();
        Predicate predicateForEmloyees = ExpressionUtils.and(predicate, EMPLOYEES_ARCHIVED.apply(false));
        employeesSheets.add(new EmployeesEntitySheet(employeeService, currentUserDetails, predicateForEmloyees, isNew, "Employees"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, EMPLOYEES_ARCHIVED.apply(isArchived));
            employeesSheets.add(new EmployeesEntitySheet(employeeService, currentUserDetails, predicateForArchived, isNew, "Archived"));
        }
        return excelExportService.exportWorkbook(employeesSheets);
    }

    @Override
    public Workbook exportExcelEquipment(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> equipmentSheets = new ArrayList<>();
        Predicate predicateforEquipment = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(false));
        equipmentSheets.add(new EquipmentEntitySheet(equipmentService, currentUserDetails, predicateforEquipment, isNew, "Equipment"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(isArchived));
            equipmentSheets.add(new EquipmentEntitySheet(equipmentService, currentUserDetails, predicateForArchived, isNew, "Archived"));
        }
        return excelExportService.exportWorkbook(equipmentSheets);
    }

    @Override
    public Workbook exportExcelDocuments(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> documentSheets = new ArrayList<>();
        Predicate predicateforDocuments = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(false));
        documentSheets.add(new DocumentsEntitySheet(documentService, currentUserDetails, predicateforDocuments, isNew, "Documents"));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, DOCUMENTS_ARCHIVED.apply(isArchived));
            documentSheets.add(new DocumentsEntitySheet(documentService, currentUserDetails, predicateForArchived, isNew, "Archived"));
        }
        return excelExportService.exportWorkbook(documentSheets);
    }

    private static class CustomersSheet implements Sheet {
        private static final String QUERY = "select * from main.customers_export_excel";

        @Override
        public List<String> getHeaders() {
            return Arrays.asList("CUSTOMER_NAME", "CVR", "CREATED", "COUNT_OF_ACTIVE_SUPPLIERS", "COUNT_OF_ALL_SUPPLIERS",
                    "COUNT_OF_ACTIVE_EMPLOYEES", "COUNT_OF_ALL_EMPLOYEES", "COUNT_OF_ACTIVE_EQUIPMENT",
                    "COUNT_OF_ALL_EQUIPMENT", "COUNT_OF_LOCATIONS", "PHONE_NUMBER", "COMPANY_EMAIL", "POSTAL_CODE",
                    "CITY", "STREET", "BUILDING NUMBER", "DISTRICT", "POINT_OF_CONTACT");
        }

        @Override
        public String getName() {
            return "Customers";
        }

        @Override
        public String getQuery() {
            return QUERY;
        }
    }

    private static class EmployeesSheet implements Sheet {
        private static final String QUERY = "select first_name, last_name, username " +
                "from employees left outer join employee_authorities on employees.id=employee_authorities.employee_id " +
                "where employee_authorities.authority in (?, ?)";

        @Override
        public List<String> getHeaders() {
            return Arrays.asList("First name", "Last name", "E-mail");
        }

        @Override
        public String getName() {
            return "Employees";
        }

        @Override
        public String getQuery() {
            return QUERY;
        }

        @Override
        public Object[] getParams() {
            return new Object[] {UserAuthorities.USER.getCode(), UserAuthorities.ADMIN.getCode()};
        }
    }
}
