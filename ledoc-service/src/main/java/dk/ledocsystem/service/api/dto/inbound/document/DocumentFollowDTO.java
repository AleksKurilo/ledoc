package dk.ledocsystem.service.api.dto.inbound.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentFollowDTO {

    private Long followerId;
    private boolean followed;
}
