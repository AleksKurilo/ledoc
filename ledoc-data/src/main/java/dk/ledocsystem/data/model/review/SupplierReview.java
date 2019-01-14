package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.model.supplier.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "supplier_reviews")
public class SupplierReview extends Review<Supplier> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_review_seq")
    @SequenceGenerator(name = "supplier_review_seq", sequenceName = "supplier_review_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Supplier subject;

    @OneToMany(mappedBy = "review", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<SupplierReviewQuestionAnswer> answers;
}
