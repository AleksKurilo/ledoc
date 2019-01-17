package dk.ledocsystem.data.model.dashboard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuperAdminStatistic {

    private Integer totalActiveCustomersCount;

    private UserStat userStat;

    private Integer usersOnline;
}
