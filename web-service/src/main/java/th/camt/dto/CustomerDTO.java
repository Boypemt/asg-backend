package th.camt.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * What a customer looks like ON THE WIRE.
 *
 * The entity has a List<SaleOrder>; this has an order-count number instead, so
 * the whole order history never travels with every customer and there is no
 * customer -> order -> customer JSON loop to break.
 *
 * Every field is an object type (Long, Integer), never a primitive: the PATCH
 * mapping works by skipping fields that are null, and an int can never be null.
 */
public class CustomerDTO {

    private Long id;
    private String displayname;
    private String address;
    private String email;
    private String phone;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    /** Read-only extra, filled in by the controller from the one-to-many. */
    @JsonProperty("order_count")
    private Integer orderCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
}
