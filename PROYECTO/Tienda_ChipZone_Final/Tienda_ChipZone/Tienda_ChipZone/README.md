# Tienda ChipZone

Proyecto grupal de Marcos de Desarrollo Web: tienda online de articulos tecnologicos.

```
Tienda_ChipZone/
├── Frontend/   HTML, CSS y JavaScript de la tienda
└── Backend/    Spring Boot: API REST + panel Thymeleaf
```

## Como ejecutarlo
1. **Base de datos** (dentro de `Backend`): `cp .env.example .env`, editar las claves y ejecutar
   `docker compose up -d mysql` (puerto 3307). Exportar `DB_USER` y `DB_PASSWORD` en la terminal.
2. **Backend** (abrir la carpeta `Backend` en el IDE, o en una terminal dentro de ella):
   ```
   .\mvnw.cmd spring-boot:run
   ```
   - API: http://localhost:8080/api/v1/productos
   - Panel: http://localhost:8080/admin/productos
3. **Frontend**: abrir `Frontend/html/productos-api.html` con Live Server (puerto 5500).
   Debe listar los productos que entrega el backend.

Detalles, pruebas y matriz de resultados: `Backend/README.md`.
