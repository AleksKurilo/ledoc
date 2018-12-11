package dk.ledocsystem.api.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.dto.inbound.ArchivedStatusDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerCreateDTO;
import dk.ledocsystem.service.api.dto.inbound.customer.CustomerEditDTO;
import dk.ledocsystem.service.api.dto.outbound.GetCustomerDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.service.api.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.CUSTOMER_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Iterable<GetCustomerDTO> getAllCustomers(Pageable pageable) {
        return customerService.getAll(pageable);
    }

    @GetMapping("/filter")
    public Iterable<GetCustomerDTO> getAllFilteredCustomers(@QuerydslPredicate(root = Customer.class) Predicate predicate,
                                                            Pageable pageable) {
        return customerService.getAll(predicate, pageable);
    }

    @GetMapping("/{customerId}")
    public GetCustomerDTO getById(@PathVariable Long customerId) {
        return customerService.getById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerId.toString()));
    }

    @RolesAllowed("super_admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetCustomerDTO createCustomer(@RequestBody CustomerCreateDTO customerCreateDTO, @CurrentUser UserDetails currentUser) {
        return customerService.createCustomer(customerCreateDTO, currentUser);
    }

    @RolesAllowed("super_admin")
    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GetCustomerDTO updateCustomerById(@PathVariable Long customerId, @RequestBody CustomerEditDTO customerEditDTO) {
        customerEditDTO.setId(customerId);
        return customerService.updateCustomer(customerEditDTO);
    }

    @RolesAllowed("super_admin")
    @PostMapping("/{customerId}/archive")
    public void changeArchivedStatus(@PathVariable Long customerId, @RequestBody ArchivedStatusDTO archivedStatusDTO) {
        customerService.changeArchivedStatus(customerId, archivedStatusDTO.isArchived());
    }

    @DeleteMapping("/{customerId}")
    public void deleteById(@PathVariable Long customerId) {
        customerService.deleteById(customerId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        customerService.deleteByIds(ids);
    }
}
