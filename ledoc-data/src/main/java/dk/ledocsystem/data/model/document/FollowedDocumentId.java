package dk.ledocsystem.data.model.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowedDocumentId implements Serializable {

    private Long employee;

    private Long followed;
}
