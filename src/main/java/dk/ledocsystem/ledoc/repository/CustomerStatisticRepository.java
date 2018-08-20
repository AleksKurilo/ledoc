package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.dashboard.CustomersStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerStatisticRepository extends JpaRepository<CustomersStatistic, Long> {}
