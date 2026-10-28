CREATE TABLE categorias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(40) NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uk_categorias_nombre UNIQUE (nombre)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

-- Toma las categorías que ya existen en productos (accesorios, celulares, gaming, laptops)
INSERT INTO categorias (nombre)
SELECT DISTINCT categoria FROM productos ORDER BY categoria;

ALTER TABLE productos ADD COLUMN categoria_id BIGINT NULL AFTER categoria;

UPDATE productos p
JOIN categorias c ON c.nombre = p.categoria
SET p.categoria_id = c.id;

ALTER TABLE productos MODIFY categoria_id BIGINT NOT NULL;

-- El texto antiguo ya no hace falta
DROP INDEX idx_productos_categoria ON productos;
ALTER TABLE productos DROP COLUMN categoria;

CREATE INDEX idx_productos_categoria_id ON productos (categoria_id);

ALTER TABLE productos
    ADD CONSTRAINT fk_productos_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT;