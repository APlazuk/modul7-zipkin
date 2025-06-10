package pl.aplazuk.inventoryms.controller;

import brave.Span;
import brave.Tracer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.service.InventoryService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService productService;
    private final Tracer tracer;

    public InventoryController(InventoryService productService, Tracer tracer) {
        this.productService = productService;
        this.tracer = tracer;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/check")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@RequestParam String category, @RequestParam Set<Long> productIds) {
        Span span = tracer.currentSpan().tag("category", category);
        span.start();
        try {
            List<ProductDTO> productsByIdAndCategory = productService.checkProductsAvailability(category, productIds);
            if (productsByIdAndCategory.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(productsByIdAndCategory);
        } finally {
            span.finish();
        }
    }
}
