package pl.aplazuk.inventoryms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aplazuk.inventoryms.dto.ProductDTO;
import pl.aplazuk.inventoryms.service.InventoryService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService productService;

    public InventoryController(InventoryService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/check")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@RequestParam String category, @RequestParam Set<Long> productIds) {
        List<ProductDTO> productsByIdAndCategory = productService.checkProductsAvailability(category, productIds);
        if (productsByIdAndCategory.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productsByIdAndCategory);
    }
}
