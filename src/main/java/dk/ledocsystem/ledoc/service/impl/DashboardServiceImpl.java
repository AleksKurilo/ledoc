package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.config.security.JwtTokenRegistry;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
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
import org.pmw.tinylog.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private static final String[] EXCEL_FIELDS = {"CUSTOMER_NAME", "CVR", "CREATED", "COUNT_OF_ACTIVE_SUPPLIERS", "COUNT_OF_ALL_SUPPLIERS", "COUNT_OF_ACTIVE_EMPLOYEES",
                                                  "COUNT_OF_ALL_EMPLOYEES", "COUNT_OF_ACTIVE_EQUIPMENT", "COUNT_OF_ALL_EQUIPMENT", "COUNT_OF_LOCATIONS", "POSTAL_CODE", "PHONE_NUMBER",
                                                  "COMPANY_EMAIL", "CITY", "STREET", "BUILDINGNUMBER", "DISTRICT", "POINT_OF_CONTACT"};

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
        fillHeaderRow(headerRow);

        for (int i=0; i<result.size(); i++) {
            Row row = sheet.createRow(i+1);
            fillRowWitdData(result.get(i), row);
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            workbook.write(bos);
            Logger.info("write file");
        } catch (IOException e) {
            Logger.error("Can't write the .xslx file", e.getMessage());
        }

        return outputStream -> outputStream.write(bos.toByteArray());
    }

    private void fillHeaderRow(Row headerRow) {
        for (int i=0; i<EXCEL_FIELDS.length; i++) {
            headerRow.createCell(i).setCellValue(EXCEL_FIELDS[i]);
        }
    }

    private void fillRowWitdData(CustomersStatistic statistic, Row row) {
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
}
