package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.service.DashboardService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private final EmployeeService employeeService;

    @Override
    public Dashboard createDashboard(Long customerId) {
        Dashboard dashboard = new Dashboard();
        Long currentUserId = employeeService.getCurrentUserId();

        dashboard.setNewEmployeesCount(employeeService.countNewEmployees(customerId, currentUserId));
        dashboard.setNewEquipmentCount(12);
        dashboard.setNewLocationsCount(55);
        return dashboard;
    }

}
