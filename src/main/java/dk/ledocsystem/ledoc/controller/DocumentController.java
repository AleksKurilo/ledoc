package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Document;
import dk.ledocsystem.ledoc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Document create(@RequestBody @Valid DocumentDTO documentDTO) {
        return documentService.createOrUpdate(documentDTO);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Document update(@RequestBody @Valid DocumentDTO documentDTO, @PathVariable long id) {
        documentDTO.setId(id);
        return documentService.createOrUpdate(documentDTO);
    }

    @PostMapping("/{documentId}/archive")
    public void changeArchivedStatus(@PathVariable Long documentId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        documentService.changeArchivedStatus(documentId, archivedStatusDTO);
    }

    @GetMapping(path = "/{id}")
    public Document getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException("document.id.not.found", id));
    }

    @GetMapping(path = "/employeeId/{employeeId}")
    public Set<Document> getByEmployeeId(@PathVariable long employeeId) {
        return documentService.getByEmployeeId(employeeId);
    }

    @GetMapping(path = "/equipmentId/{equipmentId}")
    public Set<Document> getByEquipmentId(@PathVariable long equipmentId) {
        return documentService.getByEquipmentId(equipmentId);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteById(@PathVariable long id) {
        documentService.deleteById(id);
    }
}
