package pl.aplazuk.inventoryms.dto;

import java.math.BigDecimal;

public record ProductDTO(Long id,
                         String name,
                         Integer quantity,
                         BigDecimal price
) {
}
