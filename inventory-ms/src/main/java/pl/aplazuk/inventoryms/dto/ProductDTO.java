package pl.aplazuk.inventoryms.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDTO(UUID id,
                         String productName,
                         Integer quantity,
                         BigDecimal price
) {
}
