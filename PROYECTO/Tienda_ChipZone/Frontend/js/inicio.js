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

  // Agregar al carrito desde las tarjetas de productos destacados
  document.addEventListener("click", (event) => {
    const boton = event.target.closest("[data-agregar]");
    if (!boton || boton.disabled) {
      return;
    }
    Carrito.agregar(Number(boton.dataset.agregar), 1);
    const textoOriginal = boton.textContent.trim();
    boton.textContent = "Agregado ✓";
    boton.disabled = true;
    setTimeout(() => {
      boton.textContent = textoOriginal;
      boton.disabled = false;
    }, 1200);
  });

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

  // Productos destacados: se cargan desde la API (MySQL)
  cargarDestacados();
});

async function cargarDestacados() {
  const grids = document.querySelectorAll("[data-grid-etiqueta]");
  if (grids.length === 0) {
    return;
  }

  try {
    const productos = await obtenerProductos();

    // Criterios de cada pestaña (hasta tener pedidos reales, "más vendidos" son los primeros con stock)
    const secciones = {
      "mas-vendidos": productos.filter((p) => p.stock).slice(0, 6),
      "nuevos": [...productos].sort((a, b) => b.id - a.id).slice(0, 6),
      "ofertas": productos.filter((p) => p.descuento),
    };

    grids.forEach((grid) => {
      const lista = secciones[grid.dataset.gridEtiqueta] ?? [];
      grid.innerHTML = lista.length > 0
        ? lista.map(crearTarjetaProducto).join("")
        : `<div class="col-12"><p class="text-body-secondary mb-0">No hay productos para mostrar.</p></div>`;
    });
  } catch (error) {
    console.error(error);
    grids.forEach((grid) => {
      grid.innerHTML = `<div class="col-12"><div class="alert alert-danger mb-0">No se pudieron cargar los productos. ¿Está encendido el backend?</div></div>`;
    });
  }
}

function esc(texto) {
  return String(texto ?? "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;");
}

function crearTarjetaProducto(producto) {
  const badgeHtml = producto.descuento
    ? `<span class="badge bg-accent">-${producto.descuento}%</span>`
    : "";
  const imagen = producto.imagenes[0] ?? "";
  const precioAnteriorHtml = producto.descuento
    ? `<span class="product-card__price--old">S/ ${producto.precio}</span>`
    : "";

  return `
    <div class="col-6 col-lg-4">
      <div class="product-card">
        <div class="product-card__img-wrap">
          <img src="${esc(imagen)}" alt="${esc(producto.nombre)}" loading="lazy">
        </div>
        <div class="product-card__body">
          <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
            <p class="mb-0 fw-semibold">${esc(producto.nombre)}</p>
            ${badgeHtml}
          </div>
          <p class="text-body-secondary small mb-2">${esc(producto.marca.toUpperCase())}</p>
          <p class="product-card__price mb-3">${precioAnteriorHtml}S/ ${precioFinal(producto)}</p>
          <div class="d-flex gap-2 mt-auto">
            <button class="btn btn-accent flex-grow-1" type="button" data-agregar="${producto.id}" ${producto.stock ? "" : "disabled"}>
              ${producto.stock ? "Agregar" : "Sin stock"}
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