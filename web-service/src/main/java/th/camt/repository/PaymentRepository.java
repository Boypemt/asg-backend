package th.camt.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.camt.domain.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    List<Payment> findAll();

    Payment findBySaleOrderId(Long saleOrderId);
}
