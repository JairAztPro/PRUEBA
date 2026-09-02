"use strict";

document.addEventListener("DOMContentLoaded", () => {
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
  const productos = document.querySelectorAll(".producto");
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
          <div class="alert alert-info alert-dismissible fade show bg-dark text-white border-info mb-0" role="alert">
            <i class="bi bi-funnel-fill text-accent me-2"></i>
            Resultados de búsqueda: <strong>${termBuscar || catFiltro}</strong>
            <button type="button" class="btn-close btn-close-white" onclick="clearURLFilters()" aria-label="Limpiar"></button>
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

  window.clearURLFilters = function() {
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
  //  VISTA 3: INTERACTIVIDAD DE LA FICHA EN MODAL
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
  let currentProductIcon = "bi-motherboard";

  if (modalFicha) {
    // show.bs.modal: Ocurre antes de la transición para inyectar datos de forma inmediata (S3)
    modalFicha.addEventListener("show.bs.modal", (event) => {
      const triggerButton = event.relatedTarget;
      if (triggerButton) {
        const productName = triggerButton.getAttribute("data-producto") ?? "Componente Gamer";
        basePrice = parseFloat(triggerButton.getAttribute("data-precio") ?? "899");
        currentProductIcon = triggerButton.getAttribute("data-icono") ?? "bi-cpu";
        const productDesc = triggerButton.getAttribute("data-desc") ?? "";
        const productSpecsStr = triggerButton.getAttribute("data-specs") ?? "";
        const productTagsStr = triggerButton.getAttribute("data-tags") ?? "";

        // Inyectar datos de compra
        if (modalTitle) modalTitle.textContent = productName;
        if (modalPrice) modalPrice.textContent = `S/ ${basePrice}`;
        if (modalBtnPrice) modalBtnPrice.textContent = basePrice;
        if (modalQtyInput) modalQtyInput.value = "1";
        if (modalDescription) modalDescription.textContent = productDesc;

        // Render inicial de galería y miniaturas
        changeModalImage(null, 'principal', currentProductIcon);
        configureThumbnails(currentProductIcon);

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
                  <th scope="row" class="text-white small py-2" style="width: 40%;">${parts.trim()}</th>
                  <td class="text-secondary small py-2">${parts[1].trim()}</td>
                `;
                modalSpecsTableBody.appendChild(tr);
              }
            });
          }
        }
      }
    });

    // shown.bs.modal: Establecer foco accesible por teclado una vez abierto (Rúbrica de la Semana 3)
    modalFicha.addEventListener("shown.bs.modal", () => {
      if (btnModalAdd) btnModalAdd.focus();
    });
  }

  // Controles del selector de cantidad
  window.changeModalQty = function(amount) {
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

  window.confirmAddToCart = function() {
    const qty = modalQtyInput ? modalQtyInput.value : 1;
    const name = modalTitle ? modalTitle.textContent : "Producto";
    alert(`¡Éxito! Se han agregado ${qty} unidades de "${name}" al carrito.`);
    
    if (modalFicha) {
      const modalInstance = bootstrap.Modal.getInstance(modalFicha);
      modalInstance?.hide();
    }
  };

  // Visor interactivo de galería de imágenes
  window.changeModalImage = function(element, viewType, iconClass) {
    const container = document.getElementById("modalMainImageContainer");
    const tag = document.getElementById("modalImgTag");
    const thumbnails = document.querySelectorAll(".img-thumbnail-gamer");
    
    let text = "Imagen de Referencia";
    if (viewType === 'principal') text = "Imagen de Referencia";
    if (viewType === 'secundaria') text = "Foco en Componentes";
    if (viewType === 'caja') text = "Empaque y Accesorios";

    if (container && tag) {
      container.innerHTML = `
        <i class="bi ${iconClass} text-info" style="font-size: 8rem; display: block;" aria-hidden="true"></i>
        <span class="fw-bold text-uppercase tracking-wider text-secondary small d-block mt-2" id="modalImgTag">${text}</span>
      `;
    }

    thumbnails.forEach(btn => btn.classList.remove('active'));
    if (element) {
      element.classList.add('active');
    }
  };

  function configureThumbnails(mainIcon) {
    const thumb1 = document.getElementById("thumb1");
    const thumb2 = document.getElementById("thumb2");
    const thumb3 = document.getElementById("thumb3");

    if (thumb1) {
      thumb1.innerHTML = `<i class="bi ${mainIcon} text-info fs-3" aria-hidden="true"></i>`;
      thumb1.onclick = function() { changeModalImage(this, 'principal', mainIcon); };
      thumb1.classList.add('active');
    }
    if (thumb2) {
      let secondaryIcon = 'bi-usb-c';
      if (mainIcon === 'bi-gpu-card') secondaryIcon = 'bi-fan';
      if (mainIcon === 'bi-memory') secondaryIcon = 'bi-lightning-charge';
      
      thumb2.innerHTML = `<i class="bi ${secondaryIcon} text-info fs-3" aria-hidden="true"></i>`;
      thumb2.onclick = function() { changeModalImage(this, 'secundaria', secondaryIcon); };
      thumb2.classList.remove('active');
    }
    if (thumb3) {
      thumb3.innerHTML = `<i class="bi bi-box-seam text-info fs-3" aria-hidden="true"></i>`;
      thumb3.onclick = function() { changeModalImage(this, 'caja', 'bi-box-seam'); };
      thumb3.classList.remove('active');
    }
  }
});