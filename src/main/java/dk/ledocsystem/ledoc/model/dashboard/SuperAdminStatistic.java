package dk.ledocsystem.ledoc.model.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuperAdminStatistic {

    private Integer totalActiveCustomersCount;

    private UserStat userStat;

    private Long usersOnline;
}
