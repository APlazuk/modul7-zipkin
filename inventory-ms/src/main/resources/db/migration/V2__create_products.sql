CREATE TABLE products
(
    product_id   BIGINT PRIMARY KEY,
    product_name VARCHAR(255),
    quantity     INT,
    price        DECIMAL,
    inventory_id BIGINT,
    CONSTRAINT pk_products PRIMARY KEY (product_id)
);

ALTER TABLE products
    ADD CONSTRAINT FK_PRODUCTS_ON_INVENTORY FOREIGN KEY (inventory_id) REFERENCES inventories (id);