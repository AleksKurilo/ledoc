package dk.ledocsystem.service.impl;

import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.data.model.dashboard.UserStat;
import dk.ledocsystem.data.model.security.UserAuthorities;
import dk.ledocsystem.data.repository.CustomerRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.*;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class DashboardServiceImpl implements DashboardService {

    private final EmployeeService employeeService;
    private final EquipmentService equipmentService;
    private final LocationService locationService;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final JwtTokenService tokenService;

    @Override
    public Dashboard createDashboard(UserDetails currentUserDetails) {
        Dashboard dashboard = new Dashboard();
        GetEmployeeDTO currentUser = employeeService.getByUsername(currentUserDetails.getUsername())
                .orElseThrow(IllegalStateException::new);

        dashboard.setNewEmployeesCount(employeeService.getNewEmployees(currentUserDetails, Pageable.unpaged()).getTotalElements());
        dashboard.setNewEquipmentCount(equipmentService.getNewEquipment(currentUserDetails, Pageable.unpaged()).getTotalElements());
        dashboard.setNewLocationsCount(locationService.getAllByCustomer(currentUser.getCustomerId()).size());

        return dashboard;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SuperAdminStatistic createStatistic() {
        SuperAdminStatistic statistic = new SuperAdminStatistic();
        statistic.setUsersOnline(tokenService.countUsersOnline());
        statistic.setTotalActiveCustomersCount(customerRepository.countAllByArchivedFalse());
        UserStat userStat = new UserStat();
        userStat.setTotalActiveAdminsCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.ADMIN));
        userStat.setTotalActiveEmployeesCount(employeeRepository.countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities.USER));
        statistic.setUserStat(userStat);
        return statistic;
    }
}
