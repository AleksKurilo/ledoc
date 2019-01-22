package dk.ledocsystem.data.model.logging;


import dk.ledocsystem.data.model.document.Document;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "document_logs")
public class DocumentLog extends AbstractLog {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @OneToMany(mappedBy = "log", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DocumentEditDetails> editDetails;

    public void setEditDetails(List<DocumentEditDetails> editDetails) {
        this.editDetails = editDetails;
        editDetails.forEach(details -> details.setLog(this));
    }
}
