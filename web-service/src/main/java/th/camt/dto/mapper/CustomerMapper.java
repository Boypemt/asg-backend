package th.camt.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import th.camt.domain.Customer;
import th.camt.dto.CustomerDTO;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    /**
     * orderCount is ignored here and filled in by the controller, because it is
     * derived from the one-to-many list rather than copied from a field.
     */
    @Mapping(target = "orderCount", ignore = true)
    CustomerDTO toDto(Customer entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Customer toEntity(CustomerDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateCustomerFromDto(CustomerDTO dto, @MappingTarget Customer entity);
}
