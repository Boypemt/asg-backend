package th.camt.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * One purchase. This is the centre of the model, and it carries all three
 * relationship types the assignment asks for:
 *
 *   MANY-TO-ONE  many orders belong to one customer  (this side owns customer_id)
 *   ONE-TO-MANY  one order has many order items      (mapped by OrderItem.saleOrder)
 *   ONE-TO-ONE   one order has exactly one payment   (mapped by Payment.saleOrder)
 *
 * The table is called "sale_order" because ORDER is a reserved word in SQL.
 *
 * cascade = ALL + orphanRemoval on items and payment means the lines and the
 * payment are parts of the order, not independent things: saving the order
 * saves them, and deleting the order deletes them.
 */
@Entity
@Table(name = "sale_order")
public class SaleOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;

    /** NEW, PAID, SHIPPED, CANCELLED */
    private String status;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "saleOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    public SaleOrder() {
    }

    /** Keeps both sides of the one-to-many in step. */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setSaleOrder(this);
    }

    /** Keeps both sides of the one-to-one in step. */
    public void setPaymentFor(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            payment.setSaleOrder(this);
        }
    }

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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
