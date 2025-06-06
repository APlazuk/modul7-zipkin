package pl.aplazuk.inventoryms.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq")
    @SequenceGenerator(
            name = "inventory_seq",
            sequenceName = "inventory_seq",
            allocationSize = 1
    )
    private Long id;
    private String category;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    List<Product> products;

    public Inventory(Long id, String category, List<Product> products) {
        this.id = id;
        this.category = category;
        this.products = products;
    }

    public Inventory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
