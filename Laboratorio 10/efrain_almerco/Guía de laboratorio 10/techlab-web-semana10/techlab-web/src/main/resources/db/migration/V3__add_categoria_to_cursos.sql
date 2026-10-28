-- Reto semana 9: migración incremental; V1 y V2 no se modifican.
-- 1) Columna NOT NULL con valor inicial para las filas existentes.
ALTER TABLE cursos
    ADD COLUMN categoria VARCHAR(40) NOT NULL DEFAULT 'General';

-- 2) Categorías específicas para los tres cursos del catálogo.
UPDATE cursos SET categoria = 'Frontend' WHERE id IN (1, 2);
UPDATE cursos SET categoria = 'Backend'  WHERE id = 3;

-- 3) El valor por defecto era solo transitorio: las altas futuras deberán indicarla.
ALTER TABLE cursos ALTER COLUMN categoria DROP DEFAULT;
