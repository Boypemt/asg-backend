package th.camt.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * How one order was paid for.
 *
 * Relationship: ONE-TO-ONE with SaleOrder. This side owns the foreign key
 * (saleorder_id) and it is unique, so one order can never have two payments.
 * SaleOrder points back with @OneToOne(mappedBy = "saleOrder").
 *
 * It is a separate entity and not four more columns on SaleOrder because an
 * unpaid order simply has no payment row - no nulls to interpret.
 */
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** CASH, CREDIT_CARD, TRANSFER, PROMPTPAY */
    private String method;

    private Double amount;
    private LocalDate paidDate;

    @OneToOne
    @JoinColumn(name = "saleorder_id", unique = true)
    private SaleOrder saleOrder;

    public Payment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDate paidDate) {
        this.paidDate = paidDate;
    }

    public SaleOrder getSaleOrder() {
        return saleOrder;
    }

    public void setSaleOrder(SaleOrder saleOrder) {
        this.saleOrder = saleOrder;
    }
}
