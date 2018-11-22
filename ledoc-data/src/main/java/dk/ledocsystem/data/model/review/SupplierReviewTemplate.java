package dk.ledocsystem.data.model.review;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DynamicInsert
@DynamicUpdate
@DiscriminatorValue("SUPPLIERS")
public class SupplierReviewTemplate extends ReviewTemplate {
}
