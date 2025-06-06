CREATE SEQUENCE inventory_seq START WITH 4 INCREMENT BY 1;

CREATE TABLE inventories
(
    id       BIGINT NOT NULL,
    category VARCHAR(255),
    CONSTRAINT pk_inventories PRIMARY KEY (id)
);