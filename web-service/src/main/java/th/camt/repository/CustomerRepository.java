package th.camt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.camt.domain.Customer;

/**
 * Spring Data writes the implementation. We only declare what we need.
 * findAll() is redeclared to return a List instead of an Iterable.
 */
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findAll();

    List<Customer> findByDisplaynameContainingIgnoreCase(String name);
}
