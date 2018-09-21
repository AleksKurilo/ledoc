package dk.ledocsystem.ledoc.service.impl.scheduled;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
class EmployeeReviewDateReminder {

    private final EmployeeService employeeService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Scheduled(cron = "0 0 4 * * *") // every weekday at 4:00 am
    public void checkReviewDates() {
        for (Employee employee : employeeService.getAllForReview()) {
            checkReviewDate(employee.getDetails().getNextReviewDate(), employee);
        }
    }

    private void checkReviewDate(LocalDate reviewDate, Employee employee) {
        LocalDate today = LocalDate.now();
        if (today.until(reviewDate).getDays() == 14) {
            sendReviewIn2Weeks(employee);
        } else if (today.equals(reviewDate)) {
            sendReviewToday(employee);
        }
    }

    private void sendReviewIn2Weeks(Employee employee) {
        Employee responsible = employee.getDetails().getResponsibleOfSkills();
        Map<String, Object> model = ImmutableMap.of("employeeName", employee.getName());
        EmailNotification notification = new EmailNotification(responsible.getUsername(),
                "employee_review_in_2_weeks", model);

        emailNotificationRepository.save(notification);
    }

    private void sendReviewToday(Employee employee) {
        Employee responsible = employee.getDetails().getResponsibleOfSkills();
        Map<String, Object> model = ImmutableMap.of("employeeName", employee.getName());
        EmailNotification notification = new EmailNotification(responsible.getUsername(),
                "employee_review_today", model);

        emailNotificationRepository.save(notification);
    }
}
