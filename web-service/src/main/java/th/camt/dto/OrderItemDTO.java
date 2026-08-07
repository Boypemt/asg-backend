package th.camt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One line of an order, FLATTENED.
 *
 * The entity holds a whole Product object and a whole SaleOrder object. Here
 * they are just ids plus the one field a client actually wants to display
 * (the product name), so the browser needs no second request.
 */
public class OrderItemDTO {

    private Long id;

    @JsonProperty("product_id")
    private Long productId;

    /** Read-only extra, copied from the product by the mapper. */
    @JsonProperty("product_name")
    private String productName;

    private Integer quantity;

    @JsonProperty("unit_price")
    private Double unitPrice;

    /** quantity x unitPrice, computed by the controller. */
    @JsonProperty("line_total")
    private Double lineTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
