package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.ledoc.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Dashboard createDashboard() {
        return dashboardService.createDashboard();
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
                .header("Content-Disposition", "attachment; filename=\"Customers.xslx\"")
                .body(dashboardService.exportExcelCustomers());
    }

    @RolesAllowed("super_admin")
    @GetMapping("/export/employees")
    public ResponseEntity<StreamingResponseBody> importEmployees() {
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"All_users.xslx\"")
                .body(dashboardService.exportExcelEmployees());
    }
}
