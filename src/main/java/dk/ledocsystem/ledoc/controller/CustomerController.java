package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/customer")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Iterable<Customer> getAllCustomers(@RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "0") Integer size) {
        if (page >= 0 && size > 0) {
            return customerService.getAll(PageRequest.of(page, size));
        }
        return customerService.getAll();
    }

    @GetMapping("/{customerId}")
    public Customer getById(@PathVariable Long customerId) {
        return customerService.getById(customerId)
                .orElseThrow(() -> new NotFoundException("customer.id.not.found", customerId.toString()));
    }

    @RolesAllowed("super_admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer createCustomer(@RequestBody @Valid CustomerCreateDTO customerCreateDTO) {
        return customerService.createCustomer(customerCreateDTO);
    }

    @RolesAllowed("super_admin")
    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer updateCustomerById(@PathVariable Long customerId, @RequestBody @Valid CustomerEditDTO customerEditDTO) {
        return customerService.updateCustomer(customerId, customerEditDTO);
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
