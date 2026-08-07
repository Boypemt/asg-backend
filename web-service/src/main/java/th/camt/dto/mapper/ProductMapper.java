package th.camt.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.camt.domain.Product;
import th.camt.dto.ProductDTO;

/**
 * The Assembler for Product: it copies data between the entity and the DTO so
 * the two classes never have to know about each other.
 *
 * I write the interface; MapStruct writes the class at COMPILE time. After a
 * build you can read it in
 *   web-service/target/generated-sources/annotations/th/camt/dto/mapper/ProductMapperImpl.java
 * and it is nothing but plain getters and setters - no reflection, no runtime magic.
 *
 * componentModel = "spring" makes that generated class a @Component, so it can
 * be @Autowired into a controller.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /** Entity -> DTO, for sending a product out. */
    ProductDTO toDto(Product entity);

    /**
     * DTO -> new entity, for CREATE. The id is ignored because the database
     * generates it; a client must not be able to choose a primary key.
     */
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO dto);

    /**
     * DTO -> EXISTING entity, for PATCH. This is the important one.
     *
     * nullValuePropertyMappingStrategy = IGNORE means: if a field of the DTO is
     * null, do not touch the entity. So a PATCH body of { "price": 59.99 }
     * changes the price and leaves the name and description exactly as they
     * were. Without IGNORE the missing fields would be copied over as null and
     * the update would quietly wipe them.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateProductFromDto(ProductDTO dto, @MappingTarget Product entity);
}
