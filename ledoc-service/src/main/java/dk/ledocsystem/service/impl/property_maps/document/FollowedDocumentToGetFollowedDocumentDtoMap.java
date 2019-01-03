package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.FollowedDocument;
import dk.ledocsystem.service.api.dto.outbound.document.GetFollowedDocumentDTO;
import org.modelmapper.PropertyMap;

public class FollowedDocumentToGetFollowedDocumentDtoMap extends PropertyMap<FollowedDocument, GetFollowedDocumentDTO> {

    @Override
    protected void configure() {
        map().setId(source.getFollowed().getId());
        map().setName(source.getFollowed().getName());
    }
}
