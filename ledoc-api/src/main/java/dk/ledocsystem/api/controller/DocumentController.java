package dk.ledocsystem.api.controller;

import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.DocumentDTO;
import dk.ledocsystem.service.api.dto.outbound.GetDocumentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.service.api.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;


@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO create(@RequestBody DocumentDTO documentDTO, @CurrentUser UserDetails currentUser) {
        return documentService.createOrUpdate(documentDTO, currentUser);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetDocumentDTO update(@RequestBody DocumentDTO documentDTO, @PathVariable long id, @CurrentUser UserDetails currentUser) {
        documentDTO.setId(id);
        return documentService.createOrUpdate(documentDTO, currentUser);
    }

    @PostMapping("/{documentId}/archive")
    public void changeArchivedStatus(@PathVariable Long documentId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        documentService.changeArchivedStatus(documentId, archivedStatusDTO);
    }

    @GetMapping(path = "/{id}")
    public GetDocumentDTO getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id));
    }

    @GetMapping(path = "/employeeId/{employeeId}")
    public Set<GetDocumentDTO> getByEmployeeId(@PathVariable long employeeId) {
        return documentService.getByEmployeeId(employeeId);
    }

    @GetMapping(path = "/equipmentId/{equipmentId}")
    public Set<GetDocumentDTO> getByEquipmentId(@PathVariable long equipmentId) {
        return documentService.getByEquipmentId(equipmentId);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable long id) {
        documentService.deleteById(id);
    }
}
