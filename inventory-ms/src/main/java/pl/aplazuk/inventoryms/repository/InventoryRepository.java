package pl.aplazuk.inventoryms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.aplazuk.inventoryms.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}
