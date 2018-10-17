package dk.ledocsystem.ledoc.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Data
public class EmployeeDetailsPreviewDTO {

    private String comment;

    private boolean skillAssessed;

    private Period reviewFrequency;

    private LocalDate nextReviewDate;

    private String skillResponsibleName;

    private String reviewTemplateName;

}