package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;

public interface DashboardService {

    Dashboard createDashboard();

    SuperAdminStatistic createStatistic();
}
