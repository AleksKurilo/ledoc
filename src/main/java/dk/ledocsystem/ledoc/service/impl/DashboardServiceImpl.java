package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.config.security.JwtTokenRegistry;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.model.dashboard.Dashboard;
import dk.ledocsystem.ledoc.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.ledoc.model.dashboard.UserStat;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.service.DashboardService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final JwtTokenRegistry tokenRegistry;

    @Override
    public Dashboard createDashboard() {
        Dashboard dashboard = new Dashboard();
        /*Long currentUserId = employeeService.getCurrentUserId();

        dashboard.setNewEmployeesCount(employeeService.countNewEmployees(customerId, currentUserId));
        dashboard.setNewEquipmentCount(12);
        dashboard.setNewLocationsCount(55);*/
        return dashboard;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SuperAdminStatistic createStatistic() {
        SuperAdminStatistic statistic = new SuperAdminStatistic();
        statistic.setUsersOnline(tokenRegistry.getActiveTokens());
        statistic.setTotalActiveCustomersCount(customerRepository.countAllByArchivedFalse());
        UserStat userStat = new UserStat();
        userStat.setTotalActiveAdminsCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.ADMIN));
        userStat.setTotalActiveEmployeesCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.USER));
        statistic.setUserStat(userStat);
        return statistic;
    }
}
