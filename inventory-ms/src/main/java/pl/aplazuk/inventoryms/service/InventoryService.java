package pl.aplazuk.inventoryms.service;

import brave.Span;
import brave.Tracer;
import org.springframework.stereotype.Service;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.model.Product;
import pl.aplazuk.inventoryms.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final Tracer tracer;

    public InventoryService(ProductRepository productRepository, Tracer tracer) {
        this.productRepository = productRepository;
        this.tracer = tracer;
    }

    public List<ProductDTO> getAllProducts() {
        Span span = tracer.nextSpan().name("inventory-all-products-read-db");
        span.start();
        try {
            return mapProductListToProductDtoList(productRepository.findAll());
        }finally {
            span.finish();
        }
    }

    public List<ProductDTO> checkProductsAvailability(String category, Set<Long> productIds) {
        Span span = tracer.nextSpan().name("order-inventory-check-processing").tag("category", category);
        span.start();
        try {
            if (!productIds.isEmpty()) {
                List<Product> productsByIdsAndInventoryCategory = findProductsByIdsAndInventoryCategory(category, productIds);
                return mapProductListToProductDtoList(productsByIdsAndInventoryCategory);
            }
            return Collections.emptyList();
        } finally {
            span.finish();
        }
    }

    private List<Product> findProductsByIdsAndInventoryCategory(String category, Set<Long> productIds) {
        Span dbCheckSpan = tracer.newTrace().name("inventory-products-read-db").tag("category", category).start();
        dbCheckSpan.start();
        try {
            return productRepository.findByIdsAndInventoryCategory(productIds, category);
        } finally {
            dbCheckSpan.finish();
        }
    }

    private List<ProductDTO> mapProductListToProductDtoList(List<Product> productsByIdsAndInventoryCategory) {
        return productsByIdsAndInventoryCategory.stream().map(this::mapToDto).toList();
    }

    private ProductDTO mapToDto(Product product) {
        return new ProductDTO(product.getProductId(), product.getProductName(), product.getQuantity(), product.getPrice());
    }
}
