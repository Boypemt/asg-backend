package th.camt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import th.camt.domain.Customer;
import th.camt.domain.OrderItem;
import th.camt.domain.Payment;
import th.camt.domain.Product;
import th.camt.domain.SaleOrder;
import th.camt.dto.OrderItemDTO;
import th.camt.dto.SaleOrderDTO;
import th.camt.dto.mapper.SaleOrderMapper;
import th.camt.repository.CustomerRepository;
import th.camt.repository.ProductRepository;
import th.camt.repository.SaleOrderRepository;

/**
 * REST API for sale orders - the endpoint that exercises all three
 * relationships at once.
 *
 * Creating an order means: find the customer (many-to-one), build one line per
 * product (one-to-many), and optionally attach a payment (one-to-one). Because
 * SaleOrder cascades to its items and its payment, one save() writes all of it.
 */
@RestController
@RequestMapping("/api")
public class SaleOrderController {

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SaleOrderMapper saleOrderMapper;

    /**
     * CREATE - 201 Created.
     *
     * Body:
     * {
     *   "customer_id": 1,
     *   "status": "NEW",
     *   "items": [ { "product_id": 1, "quantity": 2 } ],
     *   "payment_method": "CASH"
     * }
     *
     * 400 Bad Request when the customer or a product does not exist: the client
     * asked for something that is not there, so it is their mistake, not a
     * server failure.
     */
    @PostMapping("/sale-orders")
    public ResponseEntity<SaleOrderDTO> createSaleOrder(@RequestBody SaleOrderDTO dto) {
        if (dto.getCustomerId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Customer> customer = customerRepository.findById(dto.getCustomerId());
        if (!customer.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SaleOrder order = new SaleOrder();
        order.setCustomer(customer.get());
        order.setOrderDate(dto.getOrderDate() == null ? LocalDate.now() : dto.getOrderDate());
        order.setStatus(dto.getStatus() == null ? "NEW" : dto.getStatus());

        // one-to-many: build a line per requested product
        double total = 0.0;
        for (OrderItemDTO lineDto : dto.getItems()) {
            Optional<Product> product = productRepository.findById(lineDto.getProductId());
            if (!product.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            OrderItem item = new OrderItem();
            item.setProduct(product.get());
            item.setQuantity(lineDto.getQuantity() == null ? 1 : lineDto.getQuantity());
            // the price is copied at the time of sale, not read from the product later
            item.setUnitPrice(product.get().getPrice());
            order.addItem(item);

            total += item.getUnitPrice() * item.getQuantity();
        }

        // one-to-one: attach a payment only if the client sent a method
        if (dto.getPaymentMethod() != null) {
            Payment payment = new Payment();
            payment.setMethod(dto.getPaymentMethod());
            payment.setAmount(dto.getPaymentAmount() == null ? total : dto.getPaymentAmount());
            payment.setPaidDate(LocalDate.now());
            order.setPaymentFor(payment);
        }

        SaleOrder saved = saleOrderRepository.save(order);
        return new ResponseEntity<>(toDtoWithTotals(saved), HttpStatus.CREATED);
    }

    /** LIST - 200 OK. */
    @GetMapping("/sale-orders")
    public ResponseEntity<List<SaleOrderDTO>> listSaleOrders() {
        List<SaleOrderDTO> dtos = new ArrayList<>();
        for (SaleOrder order : saleOrderRepository.findAll()) {
            dtos.add(toDtoWithTotals(order));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /** READ one - 200 OK, or 404. */
    @GetMapping("/sale-orders/{id}")
    public ResponseEntity<SaleOrderDTO> getSaleOrder(@PathVariable Long id) {
        Optional<SaleOrder> order = saleOrderRepository.findById(id);
        if (!order.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(toDtoWithTotals(order.get()), HttpStatus.OK);
    }

    /**
     * UPDATE (PATCH) - 200 OK, or 404.
     *
     * Typically used to move the order along: { "status": "SHIPPED" }.
     * The lines and the customer are not patchable here on purpose.
     */
    @PatchMapping("/sale-orders/{id}")
    public ResponseEntity<SaleOrderDTO> patchSaleOrder(@PathVariable Long id, @RequestBody SaleOrderDTO dto) {
        Optional<SaleOrder> found = saleOrderRepository.findById(id);
        if (!found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        SaleOrder order = found.get();
        saleOrderMapper.updateSaleOrderFromDto(dto, order);
        SaleOrder saved = saleOrderRepository.save(order);

        return new ResponseEntity<>(toDtoWithTotals(saved), HttpStatus.OK);
    }

    /**
     * DELETE - 204 No Content, or 404.
     *
     * The items and the payment go with it, because SaleOrder cascades to them.
     */
    @DeleteMapping("/sale-orders/{id}")
    public ResponseEntity<String> deleteSaleOrder(@PathVariable Long id) {
        if (!saleOrderRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        saleOrderRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * The mapper copies and flattens; the money is arithmetic, so it is done
     * here.
     */
    private SaleOrderDTO toDtoWithTotals(SaleOrder order) {
        SaleOrderDTO dto = saleOrderMapper.toDto(order);

        double total = 0.0;
        for (OrderItemDTO line : dto.getItems()) {
            double lineTotal = line.getUnitPrice() * line.getQuantity();
            line.setLineTotal(lineTotal);
            total += lineTotal;
        }
        dto.setTotalAmount(total);

        return dto;
    }
}
