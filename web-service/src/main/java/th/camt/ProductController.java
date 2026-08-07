package th.camt;

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

import th.camt.domain.Product;
import th.camt.dto.ProductDTO;
import th.camt.dto.mapper.ProductMapper;
import th.camt.repository.ProductRepository;

/**
 * REST API for products.
 *
 * Every method is the same three steps:
 *   1. talk to the repository, in ENTITIES
 *   2. use the mapper to convert
 *   3. answer the client, in DTOs
 *
 * The entity never leaves this class, and the DTO never reaches the database.
 */
@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    /** CREATE - 201 Created, with the saved product (the database made the id). */
    @PostMapping("/products")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        Product saved = productRepository.save(product);
        return new ResponseEntity<>(productMapper.toDto(saved), HttpStatus.CREATED);
    }

    /** LIST - 200 OK with every product as a DTO. */
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> listProducts() {
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product product : productRepository.findAll()) {
            dtos.add(productMapper.toDto(product));
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    /** READ one - 200 OK, or 404 when the id does not exist. */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (!product.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(productMapper.toDto(product.get()), HttpStatus.OK);
    }

    /**
     * UPDATE (PATCH) - a PARTIAL update: 200 OK, or 404.
     *
     * Loading the existing row first is what makes this a merge instead of a
     * replace. The mapper then copies only the fields the client actually sent,
     * because of the IGNORE strategy on updateProductFromDto.
     */
    @PatchMapping("/products/{id}")
    public ResponseEntity<ProductDTO> patchProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Optional<Product> found = productRepository.findById(id);
        if (!found.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Product product = found.get();
        productMapper.updateProductFromDto(dto, product);
        Product saved = productRepository.save(product);

        return new ResponseEntity<>(productMapper.toDto(saved), HttpStatus.OK);
    }

    /** DELETE - 204 No Content, or 404. */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
