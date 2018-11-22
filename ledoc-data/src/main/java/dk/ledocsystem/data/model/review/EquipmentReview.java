package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.model.equipment.Equipment;
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
@Table(name = "equipment_reviews")
public class EquipmentReview extends Review<Equipment> {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_review_seq")
    @SequenceGenerator(name = "equipment_review_seq", sequenceName = "equipment_review_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Equipment subject;

    @OneToMany(mappedBy = "review", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EquipmentReviewQuestionAnswer> answers;
}
