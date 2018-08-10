package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/customer")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final CustomerService customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
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

    /*@GetMapping("/{customerCvr}")
    public Customer findCustomerByCvr(@PathVariable String cvr) {
        return customerRepository.findByCvr(cvr)
                .orElseThrow(() -> new NotFoundException("customer.cvr.not.found", cvr));
    }

    @GetMapping("/{customerPhone}")
    public Customer findByPhone(@PathVariable String customerPhone) {
        return customerRepository.findByContactPhone(customerPhone)
                .orElseThrow(() -> new NotFoundException("customer.contact.phone.not.found", customerPhone));
    }

    @GetMapping("/{contactEmail}")
    public Customer findByContactEmail(@PathVariable String contactEmail) {
        return customerRepository.findByContactEmail(contactEmail)
                .orElseThrow(() -> new NotFoundException("customer.contact.email.not.found", contactEmail));
    }

    @GetMapping("/{invoiceEmail}")
    public Customer findByInvoiceEmail(@PathVariable String invoiceEmail) {
        return customerRepository.findByInvoiceEmail(invoiceEmail)
                .orElseThrow(() -> new NotFoundException("customer.invoice.email.not.found", invoiceEmail));
    }

    @GetMapping("/{companyEmail}")
    public Customer findByCompanyEmail(@PathVariable String companyEmail) {
        return customerRepository.findByCompanyEmail(companyEmail)
                .orElseThrow(() -> new NotFoundException("customer.company.email.not.found", companyEmail));
    }*/
}
