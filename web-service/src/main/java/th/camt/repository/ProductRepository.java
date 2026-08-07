package th.camt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.camt.domain.Product;

public interface ProductRepository extends CrudRepository<Product, Long> {

    List<Product> findAll();

    /** Derived query: Spring Data builds the SQL from the method name. */
    List<Product> findByNameContainingIgnoreCase(String name);
}
