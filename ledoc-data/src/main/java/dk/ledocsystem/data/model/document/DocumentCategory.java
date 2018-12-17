package dk.ledocsystem.data.model.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "document_categories")
public class DocumentCategory {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_categories_seq")
    @SequenceGenerator(name = "document_categories_seq", sequenceName = "document_categories_seq")
    private Long id;

    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;

    @Column(name = "name_da", nullable = false, unique = true)
    private String nameDa;

    @Enumerated(EnumType.STRING)
    private DocumentCategoryType type;
}
