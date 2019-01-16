package dk.ledocsystem.data.model.logging;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEditDetails implements Serializable {

    @Id
    @Column(length = 50, nullable = false)
    private String property;

    @Column(name = "prev_value", columnDefinition = "varchar")
    private String previousValue;

    @Column(name = "cur_value", columnDefinition = "varchar")
    private String currentValue;

    public abstract<LogT extends AbstractLog> LogT getLog();
}
