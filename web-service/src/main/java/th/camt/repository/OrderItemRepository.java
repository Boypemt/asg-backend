package th.camt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.camt.domain.OrderItem;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {

    List<OrderItem> findAll();

    List<OrderItem> findBySaleOrderId(Long saleOrderId);
}
