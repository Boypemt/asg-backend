package th.camt.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.camt.domain.SaleOrder;
import th.camt.dto.SaleOrderDTO;

/**
 * The Assembler for SaleOrder.
 *
 * uses = OrderItemMapper.class tells MapStruct how to turn the List<OrderItem>
 * into a List<OrderItemDTO>: it calls the other mapper for each element instead
 * of me writing a loop.
 */
@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface SaleOrderMapper {

    /**
     * Entity -> DTO. The @Mapping lines flatten all three relationships:
     * the customer (many-to-one) and the payment (one-to-one) become plain
     * fields, and the items (one-to-many) become a list of small DTOs.
     */
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "customer.displayname", target = "customerName")
    @Mapping(source = "payment.method", target = "paymentMethod")
    @Mapping(source = "payment.amount", target = "paymentAmount")
    @Mapping(target = "totalAmount", ignore = true)
    SaleOrderDTO toDto(SaleOrder entity);

    /**
     * DTO -> EXISTING entity, for PATCH. Only the order's own simple fields can
     * be patched this way (status, orderDate).
     *
     * id, customer, items and payment are ignored because the controller
     * decides those: the id comes from the URL, and the others have to be
     * looked up or rebuilt as real rows - a client cannot hand us an object
     * graph and have it trusted.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "payment", ignore = true)
    void updateSaleOrderFromDto(SaleOrderDTO dto, @MappingTarget SaleOrder entity);
}
