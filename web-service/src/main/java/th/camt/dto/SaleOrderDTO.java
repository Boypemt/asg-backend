package th.camt.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An order ON THE WIRE. This one DTO shows all three relationships at once:
 *
 *   many-to-one : customer_id + customer_name  (flattened from the Customer object)
 *   one-to-many : items                        (a list of small line DTOs)
 *   one-to-one  : payment_method + payment_amount (flattened from the Payment object)
 *
 * The client gets everything it needs to draw an order in ONE request, which is
 * the whole point of the DTO pattern - "reduce the number of method calls".
 */
public class SaleOrderDTO {

    private Long id;

    @JsonProperty("order-date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    private String status;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("customer_name")
    private String customerName;

    private List<OrderItemDTO> items = new ArrayList<>();

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("payment_amount")
    private Double paymentAmount;

    /** Sum of every line, computed by the controller. */
    @JsonProperty("total_amount")
    private Double totalAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
