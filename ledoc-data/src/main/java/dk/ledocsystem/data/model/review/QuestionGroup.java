package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.model.Customer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@Entity
@Table(name = "question_groups")
@ToString(of = {"name"})
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionGroup {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_group_seq")
    @SequenceGenerator(name = "question_group_seq", sequenceName = "question_group_seq")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String content;

    @ManyToMany(mappedBy = "questionGroups")
    private Set<ReviewTemplate> reviewTemplates;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(joinColumns = @JoinColumn(name = "question_group_id"),
            inverseJoinColumns = @JoinColumn(name = "review_question_id"))
    @Singular
    private List<ReviewQuestion> reviewQuestions;
}
