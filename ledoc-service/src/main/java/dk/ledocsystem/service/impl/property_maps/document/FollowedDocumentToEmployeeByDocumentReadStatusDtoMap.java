package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.FollowedDocument;
import dk.ledocsystem.service.api.dto.outbound.document.EmployeeByDocumentReadStatusDTO;
import org.modelmapper.PropertyMap;

public class FollowedDocumentToEmployeeByDocumentReadStatusDtoMap extends PropertyMap<FollowedDocument, EmployeeByDocumentReadStatusDTO> {

    @Override
    protected void configure() {
        map().setName(source.getEmployee().getName());
        map().setRead(source.isRead());
    }
}
