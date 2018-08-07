package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.CustomerDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.repository.CustomerRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
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
        return customerService.getById(customerId).orElseThrow(() -> new NotFoundException(Customer.class, customerId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer createCustomer(@RequestBody @Valid CustomerDTO customerDTO) {
        return customerService.createCustomer(customerDTO);
    }

    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Customer updateCustomerById(@PathVariable Long customerId, @RequestBody @Valid CustomerDTO customerDTO) {
        return customerService.updateCustomer(customerId, customerDTO);
    }

    @DeleteMapping("/{customerId}")
    public void deleteById(@PathVariable Long customerId) {
        customerService.deleteById(customerId);
    }

    @DeleteMapping
    public void deleteByIds(@RequestParam("ids") Collection<Long> ids) {
        customerService.deleteByIds(ids);
    }

//    @GetMapping
//    public List<Customer> allNotArchived() {
//        return customerRepository.findAllByArchivedIsFalse();
//    }
//
//    @GetMapping
//    public List<Customer> allArchived() {
//        return customerRepository.findAllByArchivedIsTrue();
//    }

    @GetMapping("/{customerCvr}")
    public Customer findCustomerByCvr(@PathVariable String cvr) {
        return customerRepository.findByCvr(cvr).orElseThrow(() -> new NotFoundException(Customer.class, cvr));
    }

    @GetMapping("/{customerPhone}")
    public Customer findByPhone(@PathVariable String customerPhone) {
        return customerRepository.findByContactPhone(customerPhone).orElseThrow(() -> new NotFoundException(Customer.class, customerPhone));
    }

    @GetMapping("/{contactEmail}")
    public Customer findByContactEmail(@PathVariable String contactEmail) {
        return customerRepository.findByContactEmail(contactEmail).orElseThrow(() -> new NotFoundException(Customer.class, contactEmail));
    }

    @GetMapping("/{invoiceEmail}")
    public Customer findByInvoiceEmail(@PathVariable String invoiceEmail) {
        return customerRepository.findByInvoiceEmail(invoiceEmail).orElseThrow(() -> new NotFoundException(Customer.class, invoiceEmail));
    }

    @GetMapping("/{companyEmail}")
    public Customer findByCompanyEmail(@PathVariable String companyEmail) {
        return customerRepository.findByCompanyEmail(companyEmail).orElseThrow(() -> new NotFoundException(Customer.class, companyEmail));
    }
}
