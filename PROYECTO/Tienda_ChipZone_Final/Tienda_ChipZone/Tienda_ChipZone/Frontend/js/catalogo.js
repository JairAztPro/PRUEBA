"use strict";

document.addEventListener("DOMContentLoaded", async () => {
  // Configurar año automático en el footer
  const anioActual = document.querySelector("#anioActual");
  if (anioActual) {
    anioActual.textContent = String(new Date().getFullYear());
  }

  // ==========================================
  //  VISTA 2: FILTROS Y ORDENAMIENTO (Catálogo)
  // ==========================================
  const rango = document.querySelector("#precio");
  const valor = document.querySelector("#precioValor");
  const marcas = document.querySelectorAll(".marca");
  const stock = document.querySelector("#stock");
  let productos = [];
  const mapaProductos = new Map();
  const limpiar = document.querySelector("#limpiar");
  const selectOrden = document.querySelector("#orden");
  const contenedorProductos = document.querySelector("#productos");
  const filterAlertPlaceholder = document.getElementById("filterAlertPlaceholder");

  function aplicarFiltrosYOrden() {
    if (!contenedorProductos) return;

    const marcasActivas = [...marcas].filter(m => m.checked).map(m => m.value);
    const criterio = selectOrden ? selectOrden.value : "";
    const items = [...productos];

    // Leer parámetros activos en la URL para búsqueda o categorías provenientes de la Landing
    const urlParams = new URLSearchParams(window.location.search);
    const termBuscar = urlParams.get("buscar")?.toLowerCase() || "";
    const catFiltro = urlParams.get("categoria")?.toLowerCase() || "";

    // Notificación en pantalla de filtros externos activos
    if (termBuscar || catFiltro) {
      if (filterAlertPlaceholder) {
        filterAlertPlaceholder.innerHTML = `
          <div class="alert alert-info alert-dismissible fade show mb-0" role="alert">
            <i class="bi bi-funnel-fill text-accent me-2"></i>
            Resultados de búsqueda: <strong>${termBuscar || catFiltro}</strong>
            <button type="button" class="btn-close" onclick="clearURLFilters()" aria-label="Limpiar"></button>
          </div>
        `;
        filterAlertPlaceholder.classList.remove("d-none");
      }
    } else {
      filterAlertPlaceholder?.classList.add("d-none");
    }

    items.forEach(producto => {
      const marca = producto.dataset.marca;
      const precio = Number(producto.dataset.precio);
      const disponible = producto.dataset.stock === "true";
      const categoria = producto.dataset.categoria || "";
      const tituloHTML = producto.querySelector("h4")?.textContent.toLowerCase() || "";

      // Filtros locales
      const marcaOK = !marcasActivas.length || marcasActivas.includes(marca);
      const precioOK = precio <= Number(rango.value);
      const stockOK = !stock.checked || disponible;

      // Filtros globales (Búsqueda externa y Categorías)
      const buscarOK = !termBuscar || tituloHTML.includes(termBuscar) || marca.includes(termBuscar);
      const categoriaOK = !catFiltro || categoria.toLowerCase() === catFiltro;

      producto.classList.toggle(
        "d-none",
        !(marcaOK && precioOK && stockOK && buscarOK && categoriaOK)
      );
    });

    // Ordenar elementos
    items.sort((a, b) => {
      const precioA = Number(a.dataset.precio);
      const precioB = Number(b.dataset.precio);
      if (criterio === "Precio menor") return precioA - precioB;
      if (criterio === "Precio mayor") return precioB - precioA;
      return 0;
    });

    items.forEach(item => contenedorProductos.appendChild(item));
  }

  // ==========================================
  //  CARGA DE PRODUCTOS DESDE LA API (MySQL)
  // ==========================================
  function esc(texto) {
    return String(texto ?? "")
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#39;");
  }

  function htmlImagenTarjeta(p) {
    const imagen = imagenesProducto(p)[0];
    if (!imagen) {
      return `<i class="bi ${iconoCategoria(p)} imagen-reemplazo" aria-hidden="true"></i>`;
    }
    return `<img src="${esc(imagen)}" alt="${esc(p.nombre)}" loading="lazy" data-icono="${iconoCategoria(p)}">`;
  }

  function crearTarjeta(p) {
    const final = precioFinal(p);
    const precioHtml = p.descuento
      ? `<strong class="precio-catalogo">S/ ${final}</strong>
         <span class="text-decoration-line-through text-secundario small ms-2">S/ ${p.precio}</span>`
      : `<strong class="precio-catalogo">S/ ${final}</strong>`;
    const badge = p.descuento
      ? `<span class="badge bg-danger position-absolute top-0 start-0 m-2 badge-descuento">-${p.descuento}%</span>`
      : "";
    const boton = p.stock
      ? `<button class="btn btn-acento w-100" data-bs-toggle="modal" data-bs-target="#modalFichaProducto" data-id="${p.id}">
           <i class="bi bi-eye-fill me-1" aria-hidden="true"></i> Ver Detalles
         </button>`
      : `<button class="btn btn-secondary w-100" disabled>Sin stock</button>`;
    return `
      <div class="col producto" data-marca="${esc(p.marca)}" data-precio="${final}"
           data-stock="${p.stock}" data-categoria="${esc(p.categoria)}">
        <div class="card card-gamer h-100 d-flex flex-column">
          <div class="producto-img flex-shrink-0">
            ${badge}
            ${htmlImagenTarjeta(p)}
          </div>
          <div class="card-body d-flex flex-column p-3 flex-grow-1">
            <small class="text-secundario">${esc(p.marca.toUpperCase())}</small>
            <h4 class="h5 fw-bold mb-1">${esc(p.nombre)}</h4>
            <p class="text-secundario small mb-3">${esc(p.resumen)}</p>
            <div class="mt-auto pt-2">
              <div class="mb-2">${precioHtml}</div>
              ${boton}
            </div>
          </div>
        </div>
      </div>`;
  }

  if (contenedorProductos) {
    try {
      const lista = await obtenerProductos();
      lista.forEach(p => mapaProductos.set(p.id, p));
      contenedorProductos.innerHTML = lista.map(crearTarjeta).join("");
      productos = document.querySelectorAll(".producto");
    } catch (error) {
      console.error(error);
      contenedorProductos.innerHTML =
        `<div class="col-12"><div class="alert alert-danger">No se pudo cargar el catálogo. ¿Está encendido el backend?</div></div>`;
    }
  }

  // Eventos interactivos del catálogo
  if (contenedorProductos) {
    rango?.addEventListener("input", () => {
      valor.textContent = `S/ ${rango.value}`;
      aplicarFiltrosYOrden();
    });

    marcas.forEach(marca => {
      marca.addEventListener("change", aplicarFiltrosYOrden);
    });

    stock?.addEventListener("change", aplicarFiltrosYOrden);
    selectOrden?.addEventListener("change", aplicarFiltrosYOrden);

    limpiar?.addEventListener("click", () => {
      marcas.forEach(m => m.checked = false);
      if (stock) stock.checked = false;
      if (rango) {
        rango.value = 1500;
        valor.textContent = "S/ 1500";
      }
      if (selectOrden) selectOrden.selectedIndex = 0;

      if (window.location.search) {
        window.history.replaceState({}, document.title, window.location.pathname);
      }
      aplicarFiltrosYOrden();
    });

    // Ejecutar filtros al cargar la vista
    aplicarFiltrosYOrden();
  }

  window.clearURLFilters = function () {
    window.location.href = "catalogo.html";
  };

  // Buscador en Navbar
  const formBusqueda = document.querySelector("#formBusqueda");
  const inputBusqueda = document.querySelector("#buscarProducto");
  if (formBusqueda && inputBusqueda) {
    formBusqueda.addEventListener("submit", (event) => {
      event.preventDefault();
      const termino = inputBusqueda.value.trim();
      if (termino.length > 0) {
        window.location.href = `catalogo.html?buscar=${encodeURIComponent(termino)}`;
      }
    });
  }

  // ==========================================
  //  VISTA 3: FICHA EN MODAL
  // ==========================================
  const modalFicha = document.getElementById("modalFichaProducto");
  const modalTitle = document.getElementById("modalDynamicTitle");
  const modalPrice = document.getElementById("modalDynamicPrice");
  const modalBtnPrice = document.getElementById("modalBtnPrice");
  const modalQtyInput = document.getElementById("modalProductQty");
  const btnModalAdd = document.getElementById("btnModalAdd");
  const modalDescription = document.getElementById("modalDynamicDescription");
  const modalSpecsTableBody = document.getElementById("modalDynamicSpecsTableBody");
  const modalTagsContainer = document.getElementById("modalDynamicTagsContainer");

  let basePrice = 899;
  let productoActualId = null;
  // array de imágenes
  let currentProductImages = [];

  if (modalFicha) {
    // show.bs.modal: Ocurre antes de la transición para inyectar datos
    modalFicha.addEventListener("show.bs.modal", (event) => {
      const triggerButton = event.relatedTarget;
      if (triggerButton) {
        // Los datos del producto vienen de la API (guardados en mapaProductos)
        const p = mapaProductos.get(Number(triggerButton.dataset.id));
        if (!p) return;
        productoActualId = p.id;

        const productName = p.nombreCompleto;
        basePrice = precioFinal(p);
        currentProductImages = imagenesProducto(p);
        const productDesc = p.descripcion;
        const productSpecsStr = p.especificaciones.map(e => `${e.clave}:${e.valor}`).join(";");
        const productTagsStr = p.etiquetas.join(",");

        // Inyectar datos de compra
        if (modalTitle) modalTitle.textContent = productName;
        if (modalPrice) {
          modalPrice.textContent = `S/ ${basePrice}`;
          if (p.descuento) {
            const anterior = document.createElement("small");
            anterior.className = "text-decoration-line-through text-secondary fs-6 fw-normal ms-2";
            anterior.textContent = `S/ ${p.precio}`;
            modalPrice.append(anterior);
          }
        }
        if (modalBtnPrice) modalBtnPrice.textContent = basePrice;
        if (modalQtyInput) modalQtyInput.value = "1";
        if (modalDescription) modalDescription.textContent = productDesc;

        // Carrusel de imágenes de referencia (siempre arranca en la primera)
        renderCarruselFicha(p, currentProductImages);

        // Badges dinámicos
        if (modalTagsContainer) {
          modalTagsContainer.innerHTML = "";
          if (productTagsStr) {
            productTagsStr.split(",").forEach(tag => {
              const badge = document.createElement("span");
              badge.className = "badge bg-purple-gamer px-3 py-1 me-2 mb-2";
              badge.textContent = tag.trim();
              modalTagsContainer.appendChild(badge);
            });
          }
        }

        // Especificaciones técnicas estructuradas
        if (modalSpecsTableBody) {
          modalSpecsTableBody.innerHTML = "";
          if (productSpecsStr) {
            productSpecsStr.split(";").forEach(spec => {
              const parts = spec.split(":");
              if (parts.length === 2) {
                const tr = document.createElement("tr");
                tr.innerHTML = `
                <th scope="row" class="small py-2" style="width: 40%;">
                  ${parts[0].trim()}
                </th>
                <td class="small py-2">
                  ${parts[1].trim()}
                </td>
              `;
                modalSpecsTableBody.appendChild(tr);
              }
            });
          }
        }
      }
    });

    modalFicha.addEventListener("shown.bs.modal", () => {
      if (btnModalAdd) btnModalAdd.focus();
    });
  }

  // Controles del selector de cantidad
  window.changeModalQty = function (amount) {
    if (!modalQtyInput) return;
    let currentVal = parseInt(modalQtyInput.value);
    if (!isNaN(currentVal)) {
      let newVal = currentVal + amount;
      if (newVal >= 1) {
        modalQtyInput.value = newVal;
        if (modalBtnPrice) {
          modalBtnPrice.textContent = (basePrice * newVal).toFixed(0);
        }
      }
    }
  };

  window.confirmAddToCart = function () {
    const qty = Math.max(1, parseInt(modalQtyInput ? modalQtyInput.value : "1", 10) || 1);
    const name = modalTitle ? modalTitle.textContent : "Producto";
    if (productoActualId === null) return;

    const res = Carrito.agregar(productoActualId, qty);
    alert(`¡Listo! "${name}" está en tu carrito (${res.cantidad} unidad(es)).` +
      (res.limitado ? `\nEl máximo por producto es ${Carrito.MAX_POR_PRODUCTO}.` : ""));

    if (modalFicha) {
      const modalInstance = bootstrap.Modal.getInstance(modalFicha);
      modalInstance?.hide();
    }
  };

  // ==========================================
  // CARRUSEL DE IMÁGENES DE LA FICHA
  // ==========================================
  function renderCarruselFicha(producto, imagenes) {
    const carrusel = document.getElementById("modalCarousel");
    const inner = document.getElementById("modalCarouselInner");
    const indicadores = document.getElementById("modalCarouselIndicators");
    if (!carrusel || !inner || !indicadores) return;

    const total = imagenes.length;
    const nombre = esc(producto.nombre);

    if (total === 0) {
      // Producto sin fotos disponibles: un solo cuadro con el icono de su categoría
      inner.innerHTML = `
        <div class="carousel-item active">
          <div class="carrusel-slide">
            <i class="bi ${iconoCategoria(producto)} imagen-reemplazo" aria-hidden="true"></i>
          </div>
        </div>`;
    } else {
      inner.innerHTML = imagenes.map((url, i) => `
        <div class="carousel-item ${i === 0 ? "active" : ""}">
          <div class="carrusel-slide">
            <img src="${esc(url)}" alt="${nombre} - imagen ${i + 1} de ${total}" data-icono="${iconoCategoria(producto)}">
          </div>
        </div>`).join("");
    }

    indicadores.innerHTML = imagenes.map((_, i) => `
      <button type="button" data-bs-target="#modalCarousel" data-bs-slide-to="${i}"
        ${i === 0 ? 'class="active" aria-current="true"' : ""} aria-label="Ver imagen ${i + 1} de ${total}"></button>`).join("");

    // Con una sola imagen (o ninguna) no hacen falta botones ni indicadores
    carrusel.classList.toggle("carrusel-unico", total < 2);

    const instancia = bootstrap.Carousel.getOrCreateInstance(carrusel, { interval: false, ride: false, wrap: true });
    instancia.to(0);
  }
});
