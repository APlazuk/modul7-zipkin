package pl.aplazuk.inventoryms.dto;

import java.math.BigDecimal;

public record ProductDTO(Long id,
                         String productName,
                         Integer quantity,
                         BigDecimal price
) {
}
