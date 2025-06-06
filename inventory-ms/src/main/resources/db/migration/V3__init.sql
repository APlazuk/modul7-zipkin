INSERT INTO inventories (id, category)
VALUES (1, 'Home Accessories'),
       (2, 'Toys'),
       (3, 'Books');

INSERT INTO products (product_name, quantity, price, inventory_id)
VALUES ('mug with mickey mouse', 5, 12.55, 1),
       ('remote-controlled car', 14, 125.78, 2),
       ('lego', 2, 450.25, 2),
       ('motivation book: Jak robić na drutach i nie zwariować', 150, 47.89, 3);