package pl.aplazuk.inventoryms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.aplazuk.inventoryms.model.Product;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
                SELECT p FROM Product p 
                WHERE p.productId IN :ids 
                  AND p.inventory.category = :category
            """)
    List<Product> findByIdsAndInventoryCategory(Set<Long> ids, String category);

}
