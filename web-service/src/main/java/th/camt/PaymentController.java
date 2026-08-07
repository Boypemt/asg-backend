package th.camt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.camt.domain.Payment;
import th.camt.dto.PaymentDTO;
import th.camt.dto.mapper.PaymentMapper;
import th.camt.repository.PaymentRepository;

/**
 * Read-only API for payments.
 *
 * A payment is never created here: it is created together with its order, by
 * SaleOrderController, because the one-to-one says a payment cannot exist
 * without an order.
 */
@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentMapper paymentMapper;

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> listPayments() {
        List<PaymentDTO> dtos = new ArrayList<>();
        for (Payment payment : paymentRepository.findAll()) {
            dtos.add(paymentMapper.toDto(payment));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /** The payment of one order - 404 when that order was never paid. */
    @GetMapping("/sale-orders/{id}/payment")
    public ResponseEntity<PaymentDTO> getPaymentOfOrder(@PathVariable Long id) {
        Payment payment = paymentRepository.findBySaleOrderId(id);
        if (payment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(paymentMapper.toDto(payment), HttpStatus.OK);
    }
}
