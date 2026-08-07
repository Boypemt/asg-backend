package th.camt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import th.camt.domain.Customer;
import th.camt.domain.Product;
import th.camt.domain.SaleOrder;
import th.camt.repository.CustomerRepository;
import th.camt.repository.ProductRepository;
import th.camt.repository.SaleOrderRepository;

/**
 * Controller tests for sale orders: CREATE, LIST, UPDATE (PATCH), DELETE.
 *
 * This is the class that proves the three relationships work end to end - the
 * created order carries a customer (many-to-one), its lines (one-to-many) and
 * its payment (one-to-one), and all of it is saved by a single save() because
 * SaleOrder cascades to its parts.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SaleOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleOrderRepository saleOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Long customerId;
    private Long productId;

    /** Each test builds its own customer and product, so nothing depends on data.sql. */
    @BeforeEach
    public void createTestData() {
        Customer customer = new Customer();
        customer.setDisplayname("Test Buyer");
        customer.setEmail("buyer@test.com");
        customer.setPhone("555-9999");
        customer.setAddress("1 Test Road");
        customer.setBirthday(LocalDate.of(1995, 1, 1));
        customerId = customerRepository.save(customer).getId();

        Product product = new Product();
        product.setName("Test Widget");
        product.setPrice(25.0);
        product.setDescription("a widget for testing");
        product.setManufactureDate(LocalDate.of(2024, 1, 1));
        productId = productRepository.save(product).getId();
    }

    /**
     * CREATE: one POST builds the order, its lines and its payment.
     *
     * 2 x 25.00 = 50.00, and the unit price is taken from the product, not from
     * the request, so a client cannot invent its own price.
     */
    @Test
    public void createSaleOrderReturns201WithItemsCustomerAndPayment() throws Exception {
        String body = "{"
                + "\"customer_id\":" + customerId + ","
                + "\"status\":\"NEW\","
                + "\"items\":[{\"product_id\":" + productId + ",\"quantity\":2}],"
                + "\"payment_method\":\"CASH\""
                + "}";

        MvcResult result = mockMvc.perform(post("/api/sale-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                // many-to-one: the customer was found and flattened into the DTO
                .andExpect(jsonPath("$.customer_id").value(customerId))
                .andExpect(jsonPath("$.customer_name").value("Test Buyer"))
                // one-to-many: the line was created and priced from the product
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].product_name").value("Test Widget"))
                .andExpect(jsonPath("$.items[0].unit_price").value(25.0))
                .andExpect(jsonPath("$.items[0].line_total").value(50.0))
                .andExpect(jsonPath("$.total_amount").value(50.0))
                // one-to-one: the payment was created with the order
                .andExpect(jsonPath("$.payment_method").value("CASH"))
                .andExpect(jsonPath("$.payment_amount").value(50.0))
                .andReturn();

        Optional<SaleOrder> saved = saleOrderRepository.findById(idOf(result));
        assertTrue(saved.isPresent(), "the order was not saved");
        assertEquals(1, saved.get().getItems().size(), "the item was not cascaded");
        assertNotNull(saved.get().getPayment(), "the payment was not cascaded");
    }

    /** LIST: GET answers 200 and the new order is in the array. */
    @Test
    public void listSaleOrdersReturnsCreatedOrder() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/sale-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customer_id\":" + customerId + ",\"status\":\"NEW\","
                        + "\"items\":[{\"product_id\":" + productId + ",\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = idOf(created);

        mockMvc.perform(get("/api/sale-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == " + id + ")]").exists());
    }

    /**
     * UPDATE (PATCH): sending only the status moves the order along and leaves
     * the lines and the customer untouched.
     */
    @Test
    public void patchSaleOrderChangesStatusOnly() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/sale-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customer_id\":" + customerId + ",\"status\":\"NEW\","
                        + "\"items\":[{\"product_id\":" + productId + ",\"quantity\":2}]}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = idOf(created);

        mockMvc.perform(patch("/api/sale-orders/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"))
                // the parts the client did not send are still there
                .andExpect(jsonPath("$.customer_id").value(customerId))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.total_amount").value(50.0));
    }

    /** DELETE: 204, the order is gone, and an unknown id is 404. */
    @Test
    public void deleteSaleOrderRemovesItAndUnknownIdIs404() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/sale-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customer_id\":" + customerId + ",\"status\":\"NEW\","
                        + "\"items\":[{\"product_id\":" + productId + ",\"quantity\":1}]}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = idOf(created);

        mockMvc.perform(delete("/api/sale-orders/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/sale-orders/" + id))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/sale-orders/" + id))
                .andExpect(status().isNotFound());
    }

    /** An order for a customer who does not exist is the client's mistake: 400. */
    @Test
    public void createSaleOrderWithUnknownCustomerReturns400() throws Exception {
        mockMvc.perform(post("/api/sale-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"customer_id\":999999,\"items\":[]}"))
                .andExpect(status().isBadRequest());
    }

    private Long idOf(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
