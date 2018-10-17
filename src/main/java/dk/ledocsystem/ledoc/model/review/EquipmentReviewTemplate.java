package dk.ledocsystem.ledoc.model.review;

import dk.ledocsystem.ledoc.model.equipment.EquipmentCategory;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.Set;

@Entity
@DynamicInsert
@DynamicUpdate
@DiscriminatorValue("EQUIPMENT")
public class EquipmentReviewTemplate extends ReviewTemplate {

    @ManyToMany
    @JoinTable(name = "equipment_review_template_categories",
            joinColumns = { @JoinColumn(name = "review_template_id")},
            inverseJoinColumns = { @JoinColumn(name = "category_id") })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<EquipmentCategory> targetCategories;
}
