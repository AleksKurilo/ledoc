package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.userdetails.UserDetails;

public interface DashboardService {

    Dashboard createDashboard(UserDetails currentUserDetails);

    SuperAdminStatistic createStatistic();

    Workbook exportExcelCustomers();

    Workbook exportExcelEmployees();
}
