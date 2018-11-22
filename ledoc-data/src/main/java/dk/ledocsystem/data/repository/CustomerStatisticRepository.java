package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.dashboard.CustomersStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerStatisticRepository extends JpaRepository<CustomersStatistic, Long> {}
