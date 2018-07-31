package dk.ledocsystem.ledoc.model.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StatisticsOverview {

    private Integer totalDocumentsCount;

    private Integer totalEquipmentsCount;

    private Integer totalSuppliersCount;

    private Integer totalImprovmentsCount;

    private Integer totalEmployeesCount;

    private Integer totalLocationsCount;
}
