package pl.aplazuk.inventoryms.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.model.Inventory;
import pl.aplazuk.inventoryms.model.Product;
import pl.aplazuk.inventoryms.repository.InventoryRepository;
import pl.aplazuk.inventoryms.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InventoryService {

    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<ProductDTO> getAllProducts() {
        return mapProductListToProductDtoList(productRepository.findAll());
    }

    public List<ProductDTO> checkProductsAvailability(String category, Set<Long> productIds) {
        if (!productIds.isEmpty()) {
            List<Product> productsByIdsAndInventoryCategory = productRepository.findByIdsAndInventoryCategory(productIds, category);
            return mapProductListToProductDtoList(productsByIdsAndInventoryCategory);
        }
        return Collections.emptyList();
    }

    private List<ProductDTO> mapProductListToProductDtoList(List<Product> productsByIdsAndInventoryCategory) {
        return productsByIdsAndInventoryCategory.stream().map(this::mapToDto).toList();
    }

    private ProductDTO mapToDto(Product product) {
        return new ProductDTO(product.getProductId(), product.getProductName(), product.getQuantity(), product.getPrice());
    }
}
