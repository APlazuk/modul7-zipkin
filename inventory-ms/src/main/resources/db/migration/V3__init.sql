INSERT INTO inventories (id, category)
VALUES (1, 'home accessories'),
       (2, 'toys'),
       (3, 'books');

INSERT INTO products (product_id, product_name, quantity, price, inventory_id)
VALUES (1,'mug with mickey mouse', 5, 12.55, 1),
       (2,'remote-controlled car', 14, 125.78, 2),
       (3,'lego', 2, 450.25, 2),
       (4,'motivation book: Jak robić na drutach i nie zwariować', 150, 47.89, 3);