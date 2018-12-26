package dk.ledocsystem.api.controller;

import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.service.api.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
