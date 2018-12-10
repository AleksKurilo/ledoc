package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.UnsupportedEncodingException;

public interface DashboardService {

    Dashboard createDashboard(UserDetails currentUserDetails);

    SuperAdminStatistic createStatistic();

    Workbook exportExcelCustomers();

    Workbook exportExcelEmployees();

    Workbook exportExcelEquipment(UserDetails currentUserDetails, Predicate predicate, boolean isNew, boolean isArchived) throws UnsupportedEncodingException;
}
