package th.camt.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * What a product looks like ON THE WIRE.
 *
 * Note the renaming: the Java field is manufactureDate but the JSON says
 * "manufacture-date". The DTO owns the wire format, so a JSON name can change
 * without touching the database column, and a column can be renamed without
 * breaking any client.
 */
public class ProductDTO {

    private Long id;
    private String name;
    private Double price;
    private String description;

    @JsonProperty("manufacture-date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate manufactureDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }
}
