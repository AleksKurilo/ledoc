package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.data.model.dashboard.Dashboard;
import dk.ledocsystem.data.model.dashboard.SuperAdminStatistic;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.DashboardService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.function.Supplier;

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

    @RolesAllowed("super_admin")
    @GetMapping("/export/customers")
    public ResponseEntity<StreamingResponseBody> exportCustomers() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Customers.xlsx\"")
                .body(streamBody(dashboardService::exportExcelCustomers));
    }

    @RolesAllowed("super_admin")
    @GetMapping("/export/employees")
    public ResponseEntity<StreamingResponseBody> exportEmployees() {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"All users.xlsx\"")
                .body(streamBody(dashboardService::exportExcelEmployees));
    }

    @GetMapping("/export/equipment")
    @ResponseStatus(code = HttpStatus.OK)
    public StreamingResponseBody exportEquipment(HttpServletResponse response, @CurrentUser UserDetails currentUser, @QuerydslPredicate(root = Equipment.class) Predicate predicate,
                                                                 @RequestParam(value = "new", required = false, defaultValue = "false") boolean isNew,
                                                                 @RequestParam(value = "isarchived", required = false, defaultValue = "false") boolean isArchived) {
        response.setContentType("application/ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"All equipment.xlsx\"");
        return streamBody(() -> dashboardService.exportExcelEquipment(currentUser, predicate, isNew, isArchived));
    }

    private StreamingResponseBody streamBody(Supplier<Workbook> workbook) {
        return outputStream -> workbook.get().write(outputStream);
    }
}
