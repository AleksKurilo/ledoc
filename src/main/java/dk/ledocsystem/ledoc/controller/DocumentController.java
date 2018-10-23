package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.DocumentDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Document;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;


@RestController
@RequiredArgsConstructor
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Document create(@RequestBody DocumentDTO documentDTO) {
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return documentService.createOrUpdate(documentDTO, currentCustomer);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Document update(@RequestBody DocumentDTO documentDTO, @PathVariable long id) {
        documentDTO.setId(id);
        Customer currentCustomer = customerService.getCurrentCustomerReference();
        return documentService.createOrUpdate(documentDTO, currentCustomer);
    }

    @PostMapping("/{documentId}/archive")
    public void changeArchivedStatus(@PathVariable Long documentId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        documentService.changeArchivedStatus(documentId, archivedStatusDTO);
    }

    @GetMapping(path = "/{id}")
    public Document getById(@PathVariable long id) {
        return documentService.getById(id)
                .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, id));
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
