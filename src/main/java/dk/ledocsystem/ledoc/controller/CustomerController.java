package dk.ledocsystem.ledoc.controller;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Collection;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.CUSTOMER_ID_NOT_FOUND;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/customers")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Iterable<Customer> getAllCustomers(Pageable pageable) {
        return customerService.getAll(pageable);
    }

    @GetMapping("/filter")
    public Iterable<Customer> getAllFilteredEmployees(@QuerydslPredicate(root = Customer.class) Predicate predicate,
                                                      Pageable pageable) {
        return customerService.getAll(predicate, pageable);
    }

    @GetMapping("/{customerId}")
    public Customer getById(@PathVariable Long customerId) {
        return customerService.getById(customerId)
                .orElseThrow(() -> new NotFoundException(CUSTOMER_ID_NOT_FOUND, customerId.toString()));
    }

    @RolesAllowed("super_admin")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer createCustomer(@RequestBody CustomerCreateDTO customerCreateDTO) {
        return customerService.createCustomer(customerCreateDTO);
    }

    @RolesAllowed("super_admin")
    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer updateCustomerById(@PathVariable Long customerId, @RequestBody CustomerEditDTO customerEditDTO) {
        customerEditDTO.setId(customerId);
        return customerService.updateCustomer(customerEditDTO);
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
