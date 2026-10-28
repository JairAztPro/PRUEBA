CREATE TABLE productos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    nombre_completo VARCHAR(200) NOT NULL,
    marca VARCHAR(40) NOT NULL,
    categoria VARCHAR(40) NOT NULL,
    precio INT NOT NULL,
    descuento INT NULL,
    stock BIT(1) NOT NULL,
    resumen VARCHAR(255) NULL,
    descripcion VARCHAR(2000) NULL,
    CONSTRAINT pk_productos PRIMARY KEY (id),
    CONSTRAINT uk_productos_nombre UNIQUE (nombre),
    CONSTRAINT chk_productos_precio CHECK (precio > 0),
    CONSTRAINT chk_productos_descuento CHECK (descuento IS NULL OR descuento BETWEEN 0 AND 100)
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE INDEX idx_productos_categoria ON productos (categoria);

CREATE TABLE producto_imagenes (
    producto_id BIGINT NOT NULL,
    orden INT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    CONSTRAINT pk_producto_imagenes PRIMARY KEY (producto_id, orden),
    CONSTRAINT fk_producto_imagenes_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE TABLE producto_etiquetas (
    producto_id BIGINT NOT NULL,
    orden INT NOT NULL,
    etiqueta VARCHAR(60) NOT NULL,
    CONSTRAINT pk_producto_etiquetas PRIMARY KEY (producto_id, orden),
    CONSTRAINT fk_producto_etiquetas_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE TABLE producto_especificaciones (
    producto_id BIGINT NOT NULL,
    orden INT NOT NULL,
    clave VARCHAR(255) NULL,
    valor VARCHAR(255) NULL,
    CONSTRAINT pk_producto_especificaciones PRIMARY KEY (producto_id, orden),
    CONSTRAINT fk_producto_especificaciones_producto FOREIGN KEY (producto_id)
        REFERENCES productos (id) ON DELETE CASCADE
) ENGINE=InnoDB
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
