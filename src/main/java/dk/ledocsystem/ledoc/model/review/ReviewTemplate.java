package dk.ledocsystem.ledoc.model.review;

import dk.ledocsystem.ledoc.model.Customer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "review_templates", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
@ToString(of = {"name"})
@DiscriminatorColumn(name = "module", discriminatorType = DiscriminatorType.STRING)
public abstract class ReviewTemplate {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_template_seq")
    @SequenceGenerator(name = "review_template_seq", sequenceName = "review_template_seq")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, insertable = false, updatable = false)
    private Module module;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean isGlobal;

    @ColumnDefault("true")
    @Column(nullable = false)
    private Boolean editable = Boolean.TRUE;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "review_template_id"),
            inverseJoinColumns = @JoinColumn(name = "question_group_id"))
    private Set<QuestionGroup> questionGroups;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    // as for now all not editable reviews are referred to as simple reviews
    public boolean isSimpleReview() {
        return !editable;
    }
}
