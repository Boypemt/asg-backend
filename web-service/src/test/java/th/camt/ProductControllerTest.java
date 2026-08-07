package th.camt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

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

import th.camt.domain.Product;
import th.camt.repository.ProductRepository;

/**
 * Controller tests for products: CREATE, LIST, UPDATE (PATCH), DELETE.
 *
 * @SpringBootTest starts the whole application (with the in-memory H2 database)
 * and @AutoConfigureMockMvc gives us MockMvc, which calls the controller
 * through the real Spring MVC stack - URL matching, JSON parsing, status codes
 * - without opening a network port.
 *
 * @Transactional rolls every test back at the end, so the tests cannot affect
 * each other no matter what order they run in.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** CREATE: POST answers 201 and the product really is in the database. */
    @Test
    public void createProductReturns201AndSavesIt() throws Exception {
        String body = "{\"name\":\"Test Monitor\",\"price\":199.5,"
                + "\"description\":\"27 inch test monitor\",\"manufacture-date\":\"2024-05-01\"}";

        MvcResult result = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Monitor"))
                .andExpect(jsonPath("$.price").value(199.5))
                .andReturn();

        // the database gave it an id, and the row can be read back
        Long id = idOf(result);
        Optional<Product> saved = productRepository.findById(id);
        assertTrue(saved.isPresent(), "the product was not saved");
        assertEquals("Test Monitor", saved.get().getName());
    }

    /** LIST: GET answers 200 and returns the products, including a new one. */
    @Test
    public void listProductsReturnsAllProducts() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Listed Product\",\"price\":10.0}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name == 'Listed Product')]").exists());
    }

    /**
     * UPDATE (PATCH): sending only the price changes only the price.
     *
     * This is the test for the IGNORE strategy in ProductMapper: the name and
     * the description were not in the request body, so they must survive.
     */
    @Test
    public void patchProductChangesOnlyTheFieldsSent() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Old Name\",\"price\":50.0,\"description\":\"keep me\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = idOf(created);

        mockMvc.perform(patch("/api/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\":75.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(75.0))
                .andExpect(jsonPath("$.name").value("Old Name"))
                .andExpect(jsonPath("$.description").value("keep me"));
    }

    /** DELETE: 204 on success, and the product is gone afterwards. */
    @Test
    public void deleteProductRemovesIt() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Doomed Product\",\"price\":1.0}"))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = idOf(created);

        mockMvc.perform(delete("/api/products/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/" + id))
                .andExpect(status().isNotFound());
    }

    /** An unknown id must be 404, not a 500 stack trace. */
    @Test
    public void patchUnknownProductReturns404() throws Exception {
        mockMvc.perform(patch("/api/products/999999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"price\":1.0}"))
                .andExpect(status().isNotFound());
    }

    /** Reads the generated id out of a JSON response. */
    private Long idOf(MvcResult result) throws Exception {
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("id").asLong();
    }
}
