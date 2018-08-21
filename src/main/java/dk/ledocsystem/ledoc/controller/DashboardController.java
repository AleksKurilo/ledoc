package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.ledoc.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;

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
    @GetMapping("/importCustomers")
    public StreamingResponseBody excelCustomers(HttpServletResponse response) {
        response.setHeader("fileName", "Customers.xslx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"Customers.xslx\"");
        return dashboardService.exportExcelCustomers();
    }

    @RolesAllowed("super_admin")
    @GetMapping("/importEmployees")
    public StreamingResponseBody importEmployees(HttpServletResponse response) {
        response.setHeader("fileName", "All_users.xslx");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"All_users.xslx\"");
        return dashboardService.exportExcelEmployees();
    }
}
