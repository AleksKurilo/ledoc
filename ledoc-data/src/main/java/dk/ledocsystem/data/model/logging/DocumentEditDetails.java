package dk.ledocsystem.data.model.logging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "document_edit_details")
@NoArgsConstructor
public class DocumentEditDetails extends AbstractEditDetails {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Id
    @OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentLog log;

    public DocumentEditDetails(String property, String previousValue, String currentValue) {
        super(property, previousValue, currentValue);
    }
}
