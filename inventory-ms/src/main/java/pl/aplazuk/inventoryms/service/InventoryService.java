package pl.aplazuk.inventoryms.service;

import brave.Span;
import brave.Tracer;
import org.springframework.stereotype.Service;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.mapper.InventoryMapper;
import pl.aplazuk.inventoryms.model.Product;
import pl.aplazuk.inventoryms.repository.ProductRepository;

import java.util.List;
import java.util.Set;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final Tracer tracer;
    private final InventoryMapper inventoryMapper;

    public InventoryService(ProductRepository productRepository, Tracer tracer, InventoryMapper inventoryMapper) {
        this.productRepository = productRepository;
        this.tracer = tracer;
        this.inventoryMapper = inventoryMapper;
    }

    public List<ProductDTO> getAllProducts() {
        Span span = tracer.nextSpan().name("inventory-all-products-read-db");
        span.start();
        try {
            return inventoryMapper.mapToDtoList(productRepository.findAll());
        } finally {
            span.finish();
        }
    }

    public List<ProductDTO> checkProductsAvailability(String category, Set<Long> productIds) {
        Span span = tracer.nextSpan().name("order-inventory-check-processing").tag("category", category);
        span.start();
        try {
            if (productIds.isEmpty()) {
                throw new InventoryNotFoundException("No products available for category " + category);
            }
            List<Product> productsByIdsAndInventoryCategory = findProductsByIdsAndInventoryCategory(category, productIds);
            return inventoryMapper.mapToDtoList(productsByIdsAndInventoryCategory);
        } finally {
            span.finish();
        }
    }

    private List<Product> findProductsByIdsAndInventoryCategory(String category, Set<Long> productIds) {
        Span dbCheckSpan = tracer.newTrace().name("inventory-products-read-db").tag("category", category).start();
        dbCheckSpan.start();
        try {
            List<Product> products = productRepository.findByIdsAndInventoryCategory(productIds, category);
            if (products.isEmpty()) {
                throw new InventoryNotFoundException(
                        String.format("No products found in inventory for category '%s' and ids '%s'", category, productIds)
                );
            }
            return products;
        } finally {
            dbCheckSpan.finish();
        }
    }
}
