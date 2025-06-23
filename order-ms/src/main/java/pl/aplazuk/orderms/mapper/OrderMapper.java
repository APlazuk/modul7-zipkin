package pl.aplazuk.orderms.mapper;

import org.mapstruct.*;
import pl.aplazuk.orderms.dto.OrderDTO;
import pl.aplazuk.orderms.dto.ProductDTO;
import pl.aplazuk.orderms.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    OrderDTO toOrderDTO(Order order);

    @Mapping(target = "id", ignore = true)
    Order toOrder(OrderDTO orderDTO);

    @Mapping(source = "products", target = "products")
    @Mapping(source = "products", target = "quantity", qualifiedByName = "countQuantity")
    @Mapping(source = "products", target = "totalPrice", qualifiedByName = "countTotalPrice")
    OrderDTO toOrderDTO(OrderDTO orderDTO, List<ProductDTO> products, @Context String category);

    @Named("countQuantity")
    default int countQuantity(List<ProductDTO> productDTOList) {
        return productDTOList.size();
    }

    @Named("countTotalPrice")
    default BigDecimal countTotalPrice(List<ProductDTO> productDTOList) {
        return productDTOList.stream()
                .map(ProductDTO::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @IterableMapping(qualifiedByName = "productsWithCategory")
    List<ProductDTO> mapProductsWithCategory(List<ProductDTO> products, @Context String category);

    @Named("productsWithCategory")
    default ProductDTO productsWithCategory(ProductDTO productDTO, @Context String category) {
        productDTO.setCategory(category);
        return productDTO;
    }

    List<OrderDTO> toOrderDTOs(List<Order> orders);
}
