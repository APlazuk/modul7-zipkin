package pl.aplazuk.inventoryms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(source = "productId", target = "id")
    @Mapping(source = "productName", target = "name")
    ProductDTO mapToDto(Product product);
    List<ProductDTO> mapToDtoList(List<Product> products);

}
