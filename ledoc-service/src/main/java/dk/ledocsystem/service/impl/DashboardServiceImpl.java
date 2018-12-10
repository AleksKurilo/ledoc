package dk.ledocsystem.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.data.model.dashboard.UserStat;
import dk.ledocsystem.data.model.equipment.QEquipment;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.*;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import dk.ledocsystem.service.impl.excel.model.EntitySheet;
import dk.ledocsystem.service.impl.excel.model.Sheet;
import dk.ledocsystem.service.impl.excel.model.equipment.AbstractEntityEquipmentSheet;
import lombok.AllArgsConstructor;
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

    private static final Function<Boolean, Predicate> EQUIPMENT_ARCHIVED =
            archived -> ExpressionUtils.eqConst(QEquipment.equipment.archived, archived);

    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
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

    @Override
    public Workbook exportExcelEmployees() {
        return excelExportService.exportSheet(new EmployeesSheet());
    }

    @Override
    public Workbook exportExcelEquipment(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) {
        List<EntitySheet> equipmentSheets = new ArrayList<>();
        Predicate predicateforEquipment = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(false));
        equipmentSheets.add(new EquipmentSheet(currentUserDetails, predicateforEquipment, isNew));
        if (isArchived) {
            Predicate predicateForArchived = ExpressionUtils.and(predicate, EQUIPMENT_ARCHIVED.apply(isArchived));
            equipmentSheets.add(new EquipmentSheet(currentUserDetails, predicateForArchived, isNew, "Archived"));
        }
        return excelExportService.exportWorkbook(equipmentSheets);
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

    @AllArgsConstructor
    private class EquipmentSheet extends AbstractEntityEquipmentSheet {

        private UserDetails currentUserDetails;
        private Predicate predicate;
        private boolean isNew;
        private String name;

        public EquipmentSheet(UserDetails currentUserDetails, Predicate predicate, boolean isNew) {
            this.currentUserDetails = currentUserDetails;
            this.predicate = predicate;
            this.isNew = isNew;
            this.name = "Equipment";
        }

        @Override
        public List<String> getHeaders() {
            return Arrays.asList("NAME", "CATEGORY", "ID NUMBER", "SERIAL NUMBER", "HOME LOCATION", "CURRENT LOCATION",
                    "REVIEW RESPONSIBLE", "LOAN STATUS", "STATUS", "DUE DATE", "SUPPLIER", "REVIEW STATUS",
                    "MUST BE REVIEWED", "AUTHENTICATION TYPE", "RESPONSIBLE", "LOCAL ID");
        }

        @Override
        public List<List<String>> getRows() {
            return equipmentService.getAllForExport(currentUserDetails, predicate, isNew);
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
