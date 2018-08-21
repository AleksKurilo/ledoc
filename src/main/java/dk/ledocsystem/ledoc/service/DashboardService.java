package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface DashboardService {

    Dashboard createDashboard();

    SuperAdminStatistic createStatistic();

    StreamingResponseBody exportExcelCustomers();

    StreamingResponseBody exportExcelEmployees();
}
