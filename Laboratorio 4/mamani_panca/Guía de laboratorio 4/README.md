# TechLab APF1 — Integración profesional de interfaces con Bootstrap

**Estudiante:** _(completa tu nombre)_
**Sección:** _(completa tu sección)_
**Propósito:** Evaluación práctica APF1 de la semana 4. Portal demostrativo de
TechLab que integra HTML semántico, CSS propio, JavaScript y componentes de
Bootstrap 5.3.8 en una interfaz responsiva, interactiva y accesible.

## Cómo ejecutar el proyecto

1. Abre la carpeta `techlab-apf1/` en Visual Studio Code.
2. Instala la extensión **Live Server** si aún no la tienes.
3. Haz clic derecho sobre `index.html` y selecciona **Open with Live Server**.
4. El proyecto se abrirá en `http://127.0.0.1:5500/index.html` (el puerto
   puede variar según tu configuración).

No requiere instalación de dependencias ni servidor backend: todo el
proyecto es HTML, CSS y JavaScript del lado del cliente.

## Versiones de dependencias utilizadas

| Dependencia | Versión | Origen |
|---|---|---|
| Bootstrap (CSS y bundle JS) | 5.3.8 | CDN oficial (jsDelivr) con `integrity` y `crossorigin` |
| Bootstrap Icons | 1.13.1 | CDN oficial (jsDelivr) |
| JavaScript | ECMAScript moderno | `"use strict"`, `const`/`let`, encadenamiento opcional |

`bootstrap.bundle.min.js` ya incluye Popper, por lo que no se carga ninguna
otra versión ni un `bootstrap.min.js` adicional (evita duplicar
controladores de componentes).

## Estructura del proyecto

```
techlab-apf1/
├── index.html            Documento principal y estructura semántica
├── README.md             Este archivo
├── css/
│   └── styles.css        Personalización acotada y reglas de accesibilidad
├── js/
│   └── app.js            Eventos, estado, validación y año dinámico
├── assets/
│   └── img/              Imágenes locales de las tres rutas formativas
└── evidencias/
    └── matriz-pruebas.md Matriz de pruebas y registro de resultados
```

## Requisitos funcionales implementados

- **RF-01** Navbar colapsable con `data-bs-toggle="collapse"`; el atributo
  `aria-expanded` se actualiza automáticamente por Bootstrap al abrir y
  cerrar con mouse, `Enter` o `Space`.
- **RF-02** Los enlaces internos (`#inicio`, `#rutas`, `#horarios`,
  `#contacto`) apuntan a secciones existentes con el mismo `id`.
- **RF-03 / RF-04** Catálogo de tres rutas (Frontend, Backend, Fullstack) en
  tarjetas `col-12 col-md-6 col-xl-4` dentro de `row g-4`: 1 columna a
  360 px, 2 desde `md`, 3 desde `xl`.
- **RF-05** Tabla de horarios envuelta en `.table-responsive`, con
  `<caption>` visible y encabezados `<th scope="col">` / `scope="row"`.
- **RF-06** Cada botón "Solicitar información" define `data-curso`; el
  modal escucha `show.bs.modal` y usa `event.relatedTarget.dataset.curso`
  para completar el campo de solo lectura `#curso`.
- **RF-07** Los campos nombre, correo, modalidad y aceptación son
  `required` (con `minlength` en nombre); al enviar con datos inválidos se
  enfoca el primer control marcado como `:invalid`.
- **RF-08** El resultado válido se comunica en `#estadoFormulario`, un
  elemento con `role="status"` y `aria-live="polite"`.
- **RF-09** Al cerrar el modal (`hidden.bs.modal`) se ejecuta `form.reset()`,
  se retira `was-validated` y se limpia el mensaje de estado, para que el
  siguiente uso comience desde cero.
- **RF-10** `js/app.js` asigna `new Date().getFullYear()` al año del pie de
  página solo si el elemento existe en el DOM (comprobación defensiva).

## Requisitos de calidad implementados

- **RC-01** `lang="es"`, `<title>` descriptivo, un único `<h1>`, y uso con
  sentido de `header`, `nav`, `main`, `section`, `article`, `table`, `form`
  y `footer`.
- **RC-02** Sin atributos `style` ni `onclick` en el HTML: los estilos
  viven en `css/styles.css` y toda la lógica en `js/app.js`.
- **RC-03** Bootstrap 5.3.8 (CSS) y bundle 5.3.8 (JS) desde el mismo CDN,
  sin mezclar versiones ni duplicar scripts.
- **RC-04** Imágenes locales en `assets/img/`, con `alt` descriptivo,
  `width`, `height` y `loading="lazy"`.
- **RC-05 / RC-07** Diseño mobile-first probado en 360, 768, 1024 y
  1440 px, y verificado también con zoom del navegador al 200 %.
- **RC-06** Orden de foco lógico y `:focus-visible` restaurado con
  contraste suficiente sobre el `outline` por defecto.
- **RC-08** El error de validación combina color, ícono e ` invalid-feedback`
  textual; ningún estado depende solo del color.
- **RC-09** Todas las consultas del DOM en `app.js` están protegidas con
  comprobaciones de existencia (`if (elemento) {...}` / `?.`).
- **RC-10** Indentación coherente y comentarios que documentan la
  intención de cada bloque, no lo que ya es obvio en el código.

## Resumen de pruebas

Las pruebas de ancho de pantalla, teclado y funcionalidad del formulario se
ejecutaron con un navegador Chromium en modo headless (Playwright) contra
`index.html`, cubriendo 360×800, 768×1024, 1024×768 y 1440×900 px, además
de zoom al 200 %. El detalle completo, con capturas y resultado por caso,
está en [`evidencias/matriz-pruebas.md`](evidencias/matriz-pruebas.md).

## Limitación conocida

El registro del formulario es una simulación en el cliente: no existe
backend ni persistencia todavía. Esa integración llegará en la semana 5
con Spring Boot, tal como indica la guía.

## Créditos y licencia de imágenes

Las tres ilustraciones de `assets/img/` (`frontend.svg`, `backend.svg`,
`fullstack.svg`) son gráficos vectoriales originales, creados
específicamente para este proyecto y sin restricciones de licencia de
terceros.
