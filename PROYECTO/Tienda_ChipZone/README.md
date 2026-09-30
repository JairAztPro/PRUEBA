# Tienda ChipZone

Proyecto grupal de Marcos de Desarrollo Web: tienda online de articulos tecnologicos.

```
Tienda_ChipZone/
├── Frontend/   HTML, CSS y JavaScript de la tienda
└── Backend/    Spring Boot: API REST + panel Thymeleaf
```

## Como ejecutarlo
1. **Backend** (abrir la carpeta `Backend` en el IDE, o en una terminal dentro de ella):
   ```
   .\mvnw.cmd spring-boot:run
   ```
   - API: http://localhost:8080/api/v1/productos
   - Panel: http://localhost:8080/admin/productos
2. **Frontend**: abrir `Frontend/html/productos-api.html` con Live Server (puerto 5500).
   Debe listar los 9 productos que entrega el backend.

Detalles, pruebas y matriz de resultados: `Backend/README.md`.
