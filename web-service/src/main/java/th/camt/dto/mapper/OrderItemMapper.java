package th.camt.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import th.camt.domain.OrderItem;
import th.camt.dto.OrderItemDTO;

/**
 * Flattens one order line. The two @Mapping lines reach THROUGH the many-to-one
 * relationship: entity.product.id becomes dto.productId, entity.product.name
 * becomes dto.productName.
 *
 * lineTotal is ignored because it is arithmetic, not a copy - the controller
 * computes it.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "lineTotal", ignore = true)
    OrderItemDTO toDto(OrderItem entity);
}
