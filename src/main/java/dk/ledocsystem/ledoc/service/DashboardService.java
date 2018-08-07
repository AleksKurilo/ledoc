package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;

public interface DashboardService {

    Dashboard createDashboard(Long customerId);
}
