package th.camt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.camt.domain.SaleOrder;

public interface SaleOrderRepository extends CrudRepository<SaleOrder, Long> {

    List<SaleOrder> findAll();

    List<SaleOrder> findByStatus(String status);

    /** Walks the many-to-one back to the customer. */
    List<SaleOrder> findByCustomerId(Long customerId);
}
