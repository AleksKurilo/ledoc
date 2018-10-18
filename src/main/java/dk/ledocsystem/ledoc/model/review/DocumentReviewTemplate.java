package dk.ledocsystem.ledoc.model.review;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DynamicInsert
@DynamicUpdate
@DiscriminatorValue("DOCUMENTS")
public class DocumentReviewTemplate extends ReviewTemplate {
}
