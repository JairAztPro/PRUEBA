# Matriz de pruebas, evidencias y comprobación conceptual – Semana 7

## Matriz de pruebas manuales
Completar la columna "Obtenido" tras ejecutar la app y adjuntar captura en `evidencias/`.

| Caso | Acción | Esperado | Estado esperado | Obtenido | Evidencia |
|---|---|---|---|---|---|
| M1 | GET /cursos | 3 tarjetas, HTML sin expresiones Thymeleaf | 200 | | evidencias/M1.png |
| M2 | GET /cursos?q=Spring | 1 tarjeta, campo con "Spring" | 200 | | evidencias/M2.png |
| M3 | GET /cursos?q=NoExiste | Estado vacío, 0 resultados | 200 | | evidencias/M3.png |
| M4 | GET /cursos/2 | Detalle Bootstrap, 16 horas | 200 | | evidencias/M4.png |
| M5 | GET /cursos/999 | Vista 4xx y ruta informada | 404 | | evidencias/M5.png |
| M6 | q con 61 caracteres | Solicitud rechazada | 400 | | evidencias/M6.png |
| M7 | GET /api/v1/cursos | JSON de 3 elementos | 200 | | evidencias/M7.png |
| M8 | GET /portal | Redirección a /cursos | 302 | | evidencias/M8.png |
| R1 | GET /cursos/resumen (reto) | 3 cursos y 48 horas | 200 | | evidencias/R1.png |

## Lista de verificación responsiva y de accesibilidad
- [ ] 320, 768 y 1280 px sin desplazamiento horizontal
- [ ] Navegación solo con Tab / Shift+Tab, foco visible
- [ ] "Saltar al contenido" aparece con foco y funciona
- [ ] `label` asociado a `q`; encabezados en orden lógico
- [ ] Zoom 200 % legible y operable
- [ ] Sin CDN el contenido semántico se conserva
- [ ] Ver código fuente: sin `th:*` ni `${...}`; consola sin errores ni CSS 404

## Comprobación conceptual
1. **¿Por qué `return "cursos/lista"` en `@Controller` no escribe ese texto?** Porque sin `@ResponseBody` el String se interpreta como nombre lógico de vista: el `ViewResolver` lo resuelve a `templates/cursos/lista.html` y Thymeleaf genera el HTML. En `@RestController` el retorno sí se serializa como cuerpo.

2. **`${curso.titulo}` vs `*{titulo}`**: `${...}` evalúa contra el contexto completo (variables del Model); `*{...}` evalúa contra el objeto fijado con `th:object`, evitando repetir el prefijo.

3. **¿Por qué `@{}`?** Añade el context path, codifica variables de ruta/parámetros y evita URLs rotas o inseguras por concatenación manual.

4. **Problema que resuelve un fragmento**: evita copias de cabecera/pie; un cambio (ruta, nombre) se hace en un solo archivo.

5. **¿Por qué GET y `q` en la URL?** Consultar no cambia estado; la URL se puede compartir, marcar y recargar, y `th:value` conserva el criterio.

6. **Riesgo de `th:utext`**: interpreta HTML sin escapar; con contenido no confiable permite XSS.

7. **MockMvc demuestra** ruta, vista, atributos del Model, HTML renderizado, estados y redirecciones sin abrir puerto. **En navegador falta verificar** teclado/foco, diseño responsivo, carga de recursos (CSS/CDN), zoom y consola.

8. **¿Por qué no consultar el origen de datos desde la plantilla?** Mezcla presentación con acceso a datos/reglas, dificulta pruebas y cambios (p. ej. semana 9 con persistencia) y puede abrir conexiones por cada renderizado; la plantilla solo presenta lo que el controlador coloca en el Model.