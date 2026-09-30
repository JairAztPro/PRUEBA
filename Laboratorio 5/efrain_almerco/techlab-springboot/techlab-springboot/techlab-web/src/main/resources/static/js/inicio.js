"use strict";

document.addEventListener("DOMContentLoaded", () => {
  // Año dinámico en el footer
  const anioActual = document.querySelector("#anioActual");
  if (anioActual) {
    anioActual.textContent = String(new Date().getFullYear());
  }

  // Búsqueda: redirige al catálogo con el término como parámetro
  const formBusqueda = document.querySelector("#formBusqueda");
  const inputBusqueda = document.querySelector("#buscarProducto");
  if (formBusqueda && inputBusqueda) {
    formBusqueda.addEventListener("submit", (event) => {
      event.preventDefault();
      const termino = inputBusqueda.value.trim();
      if (termino.length === 0) {
        return;
      }
      window.location.href = `catalogo.html?buscar=${encodeURIComponent(termino)}`;
    });
  }

  // Render de los 3 grids de productos destacados (uno por pestaña)
  if (typeof PRODUCTOS !== "undefined") {
    document.querySelectorAll("[data-grid-etiqueta]").forEach((grid) => {
      const etiqueta = grid.dataset.gridEtiqueta;
      const productosFiltrados = PRODUCTOS.filter((producto) =>
        producto.etiquetas.includes(etiqueta)
      );
      grid.innerHTML = productosFiltrados.map(crearTarjetaProducto).join("");
    });
  }

  // Wishlist: delegación de eventos para que funcione en las 3 pestañas
  document.addEventListener("click", (event) => {
    const boton = event.target.closest(".btn-wishlist");
    if (!boton) {
      return;
    }
    const activo = boton.classList.toggle("is-active");
    boton.setAttribute("aria-pressed", String(activo));
    const icono = boton.querySelector("i");
    if (icono) {
      icono.classList.toggle("bi-heart", !activo);
      icono.classList.toggle("bi-heart-fill", activo);
    }
  });

  // Validación del formulario de newsletter (Constraint Validation API)
  const formNewsletter = document.querySelector("#formNewsletter");
  const estadoNewsletter = document.querySelector("#estadoNewsletter");
  if (formNewsletter && estadoNewsletter) {
    formNewsletter.addEventListener("submit", (event) => {
      event.preventDefault();
      formNewsletter.classList.add("was-validated");

      if (!formNewsletter.checkValidity()) {
        formNewsletter.querySelector(":invalid")?.focus();
        return;
      }

      estadoNewsletter.textContent = "¡Listo! Te avisaremos de nuevos lanzamientos y descuentos.";
      estadoNewsletter.classList.remove("d-none");
      formNewsletter.reset();
      formNewsletter.classList.remove("was-validated");
    });
  }
});

function crearTarjetaProducto(producto) {
  const tieneDescuento = producto.precioAnterior !== null;
  const precioAnteriorHtml = tieneDescuento
    ? `<span class="product-card__price--old">S/ ${producto.precioAnterior}</span>`
    : "";
  const badgeHtml = tieneDescuento
    ? `<span class="badge bg-accent">Oferta</span>`
    : "";

  return `
    <div class="col-6 col-lg-4">
      <div class="product-card">
        <div class="product-card__img-wrap">
          <img src="${producto.imagen}" alt="${producto.nombre}" loading="lazy">
        </div>
        <div class="product-card__body">
          <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
            <p class="mb-0 fw-semibold">${producto.nombre}</p>
            ${badgeHtml}
          </div>
          <p class="text-body-secondary small mb-2">
            <i class="bi bi-star-fill text-accent" aria-hidden="true"></i> ${producto.calificacion}
          </p>
          <p class="product-card__price mb-3">
            ${precioAnteriorHtml}S/ ${producto.precio}
          </p>
          <div class="d-flex gap-2 mt-auto">
            <button class="btn btn-accent flex-grow-1" type="button">
              Agregar
            </button>
            <button class="btn-wishlist" type="button" aria-pressed="false" aria-label="Guardar en favoritos">
              <i class="bi bi-heart" aria-hidden="true"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  `;
}