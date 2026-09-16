# Lista rápida de auditoría (Fase A · Paso 2)

| Control | Cumple | Hallazgo o corrección |
|---|---|---|
| Bootstrap CSS y JS cargan con versión 5.3.8. | Sí | `bootstrap@5.3.8` fijado en `<link>` y `<script>` con `integrity` y `crossorigin`. |
| Bootstrap Icons carga con versión 1.13.1. | Sí | `bootstrap-icons@1.13.1` fijado en el `<link>` del `<head>`. |
| No existen id duplicados. | Sí | Verificado por script (`grep` sobre todos los `id="..."` de `index.html`): cada uno aparece una sola vez. |
| Los enlaces internos llegan a una sección existente. | Sí | `#inicio`, `#rutas`, `#horarios`, `#contacto` y `#contenido` existen como `id` reales en el documento. |
| CSS y JavaScript propios cargan después de las dependencias necesarias. | Sí | `css/styles.css` se declara después de Bootstrap y Bootstrap Icons; `js/app.js` se declara después del bundle de Bootstrap, antes de cerrar `</body>`. |

Verificación adicional de estructura (sección RC-01):

- Un solo `<h1>` visible, con jerarquía `h1 → h2 → h3` sin saltos de nivel.
- `lang="es"` en `<html>`, `<meta name="viewport">` presente, `<title>`
  descriptivo.
- `header`, `nav`, `main`, `section`, `article`, `table`, `form` y `footer`
  usados con la semántica que les corresponde.
