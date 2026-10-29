-- Semana 11: conversión segura de la categoría textual heredada a una relación real.
-- No modificar V1, V2 ni V3: Flyway ya las registró en entregas anteriores.
CREATE TABLE categorias (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL,
    CONSTRAINT pk_categorias PRIMARY KEY (id),
    CONSTRAINT uk_categorias_nombre UNIQUE (nombre)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

INSERT INTO categorias (id, nombre) VALUES
    (1, 'Front-end'),
    (2, 'Back-end'),
    (3, 'Bases de datos'),
    (4, 'General');
ALTER TABLE categorias AUTO_INCREMENT = 5;

ALTER TABLE cursos ADD COLUMN categoria_id BIGINT NULL AFTER horas;
UPDATE cursos
SET categoria_id = CASE
    WHEN LOWER(categoria) IN ('frontend', 'front-end', 'front end') THEN 1
    WHEN LOWER(categoria) IN ('backend', 'back-end', 'back end') THEN 2
    WHEN LOWER(categoria) IN ('base de datos', 'bases de datos', 'database') THEN 3
    ELSE 4
END;
ALTER TABLE cursos MODIFY categoria_id BIGINT NOT NULL;
CREATE INDEX idx_cursos_categoria_id ON cursos (categoria_id);
ALTER TABLE cursos
    ADD CONSTRAINT fk_cursos_categoria
    FOREIGN KEY (categoria_id) REFERENCES categorias (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE cursos DROP COLUMN categoria;
