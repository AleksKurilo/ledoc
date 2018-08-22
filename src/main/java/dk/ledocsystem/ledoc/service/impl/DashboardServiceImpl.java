package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.config.security.JwtTokenRegistry;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.projections.EmployeeDataExcel;
import dk.ledocsystem.ledoc.exceptions.ExcelExportException;
import dk.ledocsystem.ledoc.model.dashboard.CustomersStatistic;
import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.ledoc.model.dashboard.UserStat;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.CustomerStatisticRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.DashboardService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private static final String[] EXCEL_CUSTOMERS_FIELDS = {"CUSTOMER_NAME", "CVR", "CREATED", "COUNT_OF_ACTIVE_SUPPLIERS", "COUNT_OF_ALL_SUPPLIERS", "COUNT_OF_ACTIVE_EMPLOYEES",
                                                  "COUNT_OF_ALL_EMPLOYEES", "COUNT_OF_ACTIVE_EQUIPMENT", "COUNT_OF_ALL_EQUIPMENT", "COUNT_OF_LOCATIONS", "POSTAL_CODE", "PHONE_NUMBER",
                                                  "COMPANY_EMAIL", "CITY", "STREET", "BUILDINGNUMBER", "DISTRICT", "POINT_OF_CONTACT"};

    private static final String[] EXCEL_EMPLOYEES_FIELDS = {"First name(s)", "Last name", "E-mail"};

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final JwtTokenRegistry tokenRegistry;
    private final CustomerStatisticRepository customerStatisticRepository;

    @Override
    public Dashboard createDashboard() {
        Dashboard dashboard = new Dashboard();
        Long currentUserId = employeeService.getCurrentUserId();
        Long customerId = customerService.getCurrentCustomerReference().getId();

        dashboard.setNewEmployeesCount(employeeService.countNewEmployees(customerId, currentUserId));
        dashboard.setNewEquipmentCount(12);
        dashboard.setNewLocationsCount(55);

        return dashboard;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SuperAdminStatistic createStatistic() {
        SuperAdminStatistic statistic = new SuperAdminStatistic();
        statistic.setUsersOnline(tokenRegistry.getActiveTokens());
        statistic.setTotalActiveCustomersCount(customerRepository.countAllByArchivedFalse());
        UserStat userStat = new UserStat();
        userStat.setTotalActiveAdminsCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.ADMIN));
        userStat.setTotalActiveEmployeesCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.USER));
        statistic.setUserStat(userStat);
        return statistic;
    }

    @Override
    public StreamingResponseBody exportExcelCustomers() {
        List<CustomersStatistic> result = customerStatisticRepository.findAll();
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        fillHeaderRow(headerRow, EXCEL_CUSTOMERS_FIELDS);

        for (int i=0; i<result.size(); i++) {
            Row row = sheet.createRow(i+1);
            fillRowCustomer(result.get(i), row);
        }

        return writeStream(workbook);
    }

    @Override
    public StreamingResponseBody exportExcelEmployees() {
        List<EmployeeDataExcel> result = employeeRepository.findAllByAuthoritiesIn(Arrays.asList(UserAuthorities.USER, UserAuthorities.ADMIN));
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();

        Row headerRow = sheet.createRow(0);
        fillHeaderRow(headerRow, EXCEL_EMPLOYEES_FIELDS);

        for (int i=0; i<result.size(); i++) {
            Row row = sheet.createRow(i+1);
            fillRowEmployee(result.get(i), row);
        }

        return writeStream(workbook);
    }

    private void fillHeaderRow(Row headerRow, String[] array) {
        for (int i=0; i<array.length; i++) {
            headerRow.createCell(i).setCellValue(array[i]);
        }
    }

    private void fillRowCustomer(CustomersStatistic statistic, Row row) {
        row.createCell(0).setCellValue(statistic.getCustomerName());
        row.createCell(1).setCellValue(statistic.getCvr());
        row.createCell(2).setCellValue(statistic.getDateOfCreation().format(DateTimeFormatter.ISO_DATE));
        row.createCell(3).setCellValue(statistic.getActiveSuppliers());
        row.createCell(4).setCellValue(statistic.getAllSuppliers());
        row.createCell(5).setCellValue(statistic.getActiveEmployees());
        row.createCell(6).setCellValue(statistic.getAllEmployees());
        row.createCell(7).setCellValue(statistic.getActiveEquipments());
        row.createCell(8).setCellValue(statistic.getAllEquipments());
        row.createCell(9).setCellValue(statistic.getLocations());
        row.createCell(10).setCellValue(statistic.getPostalCode());
        row.createCell(11).setCellValue(statistic.getPhoneNumber());
        row.createCell(12).setCellValue(statistic.getCompanyEmail());
        row.createCell(13).setCellValue(statistic.getCity());
        row.createCell(14).setCellValue(statistic.getStreet());
        row.createCell(15).setCellValue(statistic.getBuildingNumber());
        row.createCell(16).setCellValue(statistic.getDistrict());
        row.createCell(17).setCellValue(statistic.getPointOfContact());
    }

    private void fillRowEmployee(EmployeeDataExcel dataExcel,  Row row) {
        row.createCell(0).setCellValue(dataExcel.getFirstName());
        row.createCell(1).setCellValue(dataExcel.getLastName());
        row.createCell(2).setCellValue(dataExcel.getUsername());
    }

    private StreamingResponseBody writeStream(XSSFWorkbook workbook) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
        }
        catch (IOException e) {
            throw new ExcelExportException("dashboard.excel.error", e.getMessage());
        }

        return outputStream -> outputStream.write(bos.toByteArray());
    }
}
