package th.camt.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import th.camt.domain.Payment;
import th.camt.dto.PaymentDTO;

/** Flattens the one-to-one: entity.saleOrder.id becomes dto.saleOrderId. */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "saleOrder.id", target = "saleOrderId")
    PaymentDTO toDto(Payment entity);
}
