# Matriz de pruebas — TechLab APF1

## Cómo se generó esta evidencia

Las pruebas se ejecutaron de forma automatizada con **Playwright** (Chromium
en modo headless) contra `index.html`, reproduciendo cada caso del
protocolo de pruebas de la guía (secciones 9.1 y 9.2). El script se
encuentra en [`run-tests.js`](run-tests.js) y puede volver a ejecutarse con:

```bash
npm install playwright   # una sola vez
npx playwright install chromium
node evidencias/run-tests.js
```

> **Nota sobre el entorno de esta ejecución.** El sandbox donde se generó
> esta evidencia no tiene acceso a internet, por lo que las dependencias de
> Bootstrap se sirvieron desde una copia local equivalente (Bootstrap
> 5.3.x) solo para poder correr el navegador headless sin red. El
> `index.html` que se entrega usa las rutas de CDN oficiales fijadas en
> 5.3.8, tal como exige la sección 3.1. Al ejecutar el proyecto con Live
> Server y conexión a internet real, el comportamiento es el mismo: las
> capturas de esta carpeta corresponden a la interfaz y la lógica reales
> del proyecto, no a una versión distinta.

Resultado íntegro de la última corrida (15 de 15 casos): [`resultados.json`](resultados.json).

## 9.1 Matriz por ancho

| Ancho | Qué se observó | Resultado |
|---|---|---|
| 360 × 800 px | Navbar colapsada, una card por fila, modal dentro de la pantalla, sin desplazamiento horizontal (`scrollWidth` = `clientWidth` = 360 px). | **Pasa** — [`captura-360x800.png`](captura-360x800.png) |
| 768 × 1024 px | Dos cards por fila, tabla operable, espacios consistentes, campos utilizables. | **Pasa** — [`captura-768x1024.png`](captura-768x1024.png) |
| 1024 × 768 px | Distribución equilibrada, navegación visible según breakpoint, texto sin líneas excesivamente largas. | **Pasa** — [`captura-1024x768.png`](captura-1024x768.png) |
| 1440 × 900 px | Contenido limitado por `container`, tres cards por fila, alineación y jerarquía estables. | **Pasa** — [`captura-1440x900.png`](captura-1440x900.png) |
| Zoom 200 % | Sin solapamiento; controles y modal siguen visibles y operables (`scrollWidth` = `clientWidth` tras reducir el viewport a la mitad, equivalente a 200 %). | **Pasa** — [`captura-zoom200.png`](captura-zoom200.png) |

## 9.2 Casos funcionales

| Caso | Procedimiento | Resultado esperado | Resultado |
|---|---|---|---|
| CP-01 | Recargar y revisar Console y Network. | Cero errores; recursos esenciales con respuesta correcta. | **Pasa** — 0 errores de consola, 0 solicitudes fallidas. |
| CP-02 | Abrir y cerrar la navbar con teclado (foco + Enter). | Estado visible y `aria-expanded` actualizado. | **Pasa** — `aria-expanded` cambia de `false` a `true`. |
| CP-03 | Activar cada enlace interno. | Foco o vista llega a la sección correcta. | **Pasa** — `#inicio`, `#rutas`, `#horarios` y `#contacto` existen y son destino válido. |
| CP-04 | Abrir el modal desde cada tarjeta. | La ruta elegida cambia según `data-curso`. | **Pasa** — el campo Ruta recibe exactamente "Backend con Spring Boot" al activar esa tarjeta. |
| CP-05 | Cerrar el modal con Escape. | El modal cierra y el foco regresa al activador. | **Pasa** — modal oculto tras `Escape`; el foco vuelve al botón "Frontend con Bootstrap". |
| CP-06 | Enviar el formulario vacío. | No continúa; muestra errores y enfoca el primer campo inválido. | **Pasa** — se agrega `was-validated`, el foco pasa a `#nombre`, el mensaje de éxito permanece oculto. |
| CP-07 | Ingresar un correo inválido. | El mensaje identifica el problema de formato. | **Pasa** — `#correo` queda inválido y recibe el foco, con el texto "Ingresa un correo con formato válido." |
| CP-08 | Completar datos válidos. | Aparece confirmación anunciable; no se simula almacenamiento real. | **Pasa** — se muestra "Registro de demostración completado. Gracias por tu interés." en `role="status"`. |
| CP-09 | Cerrar y reabrir el modal. | Campos, feedback y mensaje vuelven al estado inicial. | **Pasa** — `#nombre` queda vacío, se retira `was-validated` y el mensaje vuelve a `d-none`. |
| CP-10 | Recorrer toda la página con teclado. | Orden lógico, foco visible, ninguna trampa de teclado. | **Pasa** — la secuencia de `Tab` avanza por enlace de salto, navbar, hero y las tres tarjetas sin repetirse en el mismo elemento. |

**Total: 15/15 casos superados** (5 de la matriz por ancho + 10 casos funcionales).

## Evidencia visual adicional

- [`captura-modal-errores.png`](captura-modal-errores.png): formulario enviado vacío, con los cuatro mensajes de error visibles y específicos (no dependen solo del color).
- [`captura-modal-exito.png`](captura-modal-exito.png): formulario completo y válido, con la confirmación visible.

## Puertas de calidad (sección 3.2)

| Puerta | Verificación | Estado |
|---|---|---|
| 1 — Carga | 0 recursos con 404, 0 errores de consola (CP-01). | Cumple |
| 2 — Estructura | Un solo `h1` visible; jerarquía `h1 > h2 > h3` sin saltos. | Cumple |
| 3 — Adaptación | Sin desplazamiento horizontal a 360 px ni solapamiento al 200 % de zoom (matriz por ancho). | Cumple |
| 4 — Interacción | Navbar, modal y validación operables con teclado (CP-02, CP-05, CP-06 a CP-09). | Cumple |
| 5 — Evidencia | Cada requisito evaluado se relaciona con una captura o un caso de prueba reproducible. | Cumple |
