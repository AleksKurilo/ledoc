package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.ledoc.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.RolesAllowed;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @RolesAllowed("super_admin")
    @GetMapping("/statistic")
    public SuperAdminStatistic statistic() {
        return dashboardService.createStatistic();
    }
}
