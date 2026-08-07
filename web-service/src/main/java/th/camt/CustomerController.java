package th.camt;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.camt.domain.Customer;
import th.camt.dto.CustomerDTO;
import th.camt.dto.mapper.CustomerMapper;
import th.camt.repository.CustomerRepository;

/**
 * REST API for customers.
 *
 * Read plus create only - a customer is not deleted from the shop while their
 * orders still refer to them.
 */
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @PostMapping("/customers")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO dto) {
        Customer saved = customerRepository.save(customerMapper.toEntity(dto));
        return new ResponseEntity<>(toDtoWithOrderCount(saved), HttpStatus.CREATED);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDTO>> listCustomers() {
        List<CustomerDTO> dtos = new ArrayList<>();
        for (Customer customer : customerRepository.findAll()) {
            dtos.add(toDtoWithOrderCount(customer));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (!customer.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(toDtoWithOrderCount(customer.get()), HttpStatus.OK);
    }

    /**
     * The mapper copies the plain fields; the order count is derived from the
     * one-to-many list, so it is filled in here.
     */
    private CustomerDTO toDtoWithOrderCount(Customer customer) {
        CustomerDTO dto = customerMapper.toDto(customer);
        dto.setOrderCount(customer.getOrders() == null ? 0 : customer.getOrders().size());
        return dto;
    }
}
