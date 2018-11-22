package dk.ledocsystem.api.controller;

import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.service.api.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public Dashboard createDashboard(@CurrentUser UserDetails currentUser) {
        return dashboardService.createDashboard(currentUser);
    }

    @RolesAllowed("super_admin")
    @GetMapping("/statistic")
    public SuperAdminStatistic statistic() {
        return dashboardService.createStatistic();
    }

    @RolesAllowed("super_admin")
    @GetMapping("/export/customers")
    public ResponseEntity<StreamingResponseBody> excelCustomers() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Customers.xslx\"")
                .body(writeByteArray(dashboardService.exportExcelCustomers()));
    }

    @RolesAllowed("super_admin")
    @GetMapping("/export/employees")
    public ResponseEntity<StreamingResponseBody> importEmployees() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"All_users.xslx\"")
                .body(writeByteArray(dashboardService.exportExcelEmployees()));
    }

    private StreamingResponseBody writeByteArray(byte[] byteArray) {
        return outputStream -> outputStream.write(byteArray);
    }
}
