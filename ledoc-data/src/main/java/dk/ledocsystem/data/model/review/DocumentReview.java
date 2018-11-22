package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.model.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "document_reviews")
public class DocumentReview extends Review<Document> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_review_seq")
    @SequenceGenerator(name = "document_review_seq", sequenceName = "document_review_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Document subject;

    @OneToMany(mappedBy = "review", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<DocumentReviewQuestionAnswer> answers;
}
