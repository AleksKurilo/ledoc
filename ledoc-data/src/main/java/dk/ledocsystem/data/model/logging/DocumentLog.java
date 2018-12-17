package dk.ledocsystem.data.model.logging;


import dk.ledocsystem.data.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "DocumentLog")
@Table(name = "document_logs")
public class DocumentLog extends AbstractLog {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;
}
