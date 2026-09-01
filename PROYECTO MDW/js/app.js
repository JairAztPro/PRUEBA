"use strict";

const formCheckout = document.querySelector("#formCheckout");

if (formCheckout) {
  formCheckout.addEventListener("submit", (event) => {
    event.preventDefault();
    event.stopPropagation();

    formCheckout.classList.add("was-validated");

    if (!formCheckout.checkValidity()) {
      const primerCampoInvalido = formCheckout.querySelector(":invalid");
      primerCampoInvalido?.focus();
      return;
    }

    alert("¡Compra procesada con éxito! (Modo demostración)");
    formCheckout.reset();
    formCheckout.classList.remove("was-validated");
  });
}

document.addEventListener("DOMContentLoaded", () => {
  const rango = document.querySelector("#precio");
  const valor = document.querySelector("#precioValor");
  const marcas = document.querySelectorAll(".marca");
  const stock = document.querySelector("#stock");
  const productos = document.querySelectorAll(".producto");
  const limpiar = document.querySelector("#limpiar");
  const selectOrden = document.querySelector("#orden");
  const contenedorProductos = document.querySelector("#productos");

  if (!contenedorProductos) return;

  function aplicarFiltrosYOrden() {
    const marcasActivas = [...marcas]
      .filter((m) => m.checked)
      .map((m) => m.value);

    const criterio = selectOrden ? selectOrden.value : "";
    const items = [...productos];

    // 1. Filtrar
    items.forEach((producto) => {
      const marca = producto.dataset.marca;
      const precio = Number(producto.dataset.precio);
      const disponible = producto.dataset.stock === "true";

      const marcaOK = !marcasActivas.length || marcasActivas.includes(marca);
      const precioOK = precio <= Number(rango.value);
      const stockOK = !stock.checked || disponible;

      producto.classList.toggle("d-none", !(marcaOK && precioOK && stockOK));
    });

    // 2. Ordenar
    items.sort((a, b) => {
      const precioA = Number(a.dataset.precio);
      const precioB = Number(b.dataset.precio);

      if (criterio === "Precio menor") return precioA - precioB;
      if (criterio === "Precio mayor") return precioB - precioA;
      return 0;
    });

    // 3. Reordenar físicamente en el DOM
    items.forEach((item) => contenedorProductos.appendChild(item));
  }

  // Eventos
  rango?.addEventListener("input", () => {
    valor.textContent = `S/ ${rango.value}`;
    aplicarFiltrosYOrden();
  });

  marcas.forEach((marca) => {
    marca.addEventListener("change", aplicarFiltrosYOrden);
  });

  stock?.addEventListener("change", aplicarFiltrosYOrden);

  selectOrden?.addEventListener("change", aplicarFiltrosYOrden);

  limpiar?.addEventListener("click", () => {
    marcas.forEach((m) => (m.checked = false));
    stock.checked = false;
    rango.value = 1500;
    valor.textContent = "S/ 1500";
    if (selectOrden) selectOrden.selectedIndex = 0;

    aplicarFiltrosYOrden();
  });
});

//vista 3 ficha productos
const modalFicha = document.getElementById("modalProducto"); // Reutiliza el ID de tu compañero
const modalTitle = document.getElementById("modalDynamicTitle");
const modalPrice = document.getElementById("modalDynamicPrice");
const modalBtnPrice = document.getElementById("modalBtnPrice");
const modalQtyInput = document.getElementById("modalProductQty");
const btnModalAdd = document.getElementById("btnModalAdd");
const modalDescription = document.getElementById("modalDynamicDescription");
const modalSpecsTableBody = document.getElementById(
  "modalDynamicSpecsTableBody",
);
const modalTagsContainer = document.getElementById("modalDynamicTagsContainer");

let basePrice = 899;
let currentProductIcon = "bi-motherboard";

if (modalFicha) {
  // Escuchar la apertura del modal para rellenar los datos de forma adaptativa
  modalFicha.addEventListener("show.bs.modal", (event) => {
    const triggerButton = event.relatedTarget; // El botón "Agregar" o "Ver" que disparó el modal
    if (triggerButton) {
      // Encontrar la tarjeta de producto que contiene al botón presionado
      const card = triggerButton.closest(".card-gamer");

      if (card) {
        // 1. Extraer los textos e iconos nativos estructurados
        const rawTitle =
          card.querySelector("h5")?.textContent.trim() ?? "Componente Gamer";
        const brand =
          card.querySelector("small")?.textContent.trim() ?? "TechLab";
        const rawPriceText =
          card.querySelector(".precio-catalogo")?.textContent.trim() ??
          "S/ 899";

        // Limpiar el texto del precio para calcular multiplicaciones
        basePrice = parseFloat(rawPriceText.replace(/[^\d]/g, "")) || 899;

        // Extraer la clase del icono de imagen
        const iconElement = card.querySelector(".producto-img i");
        if (iconElement) {
          currentProductIcon =
            Array.from(iconElement.classList).find((c) =>
              c.startsWith("bi-"),
            ) ?? "bi-cpu";
        } else {
          currentProductIcon = "bi-cpu";
        }

        // 2. Definir fichas técnicas personalizadas en base al producto detectado
        let productDesc =
          "Componente de hardware de alto rendimiento, ideal para configuraciones extremas y gaming competitivo.";
        let specs =
          "Garantía:12 meses;Compatibilidad:Estándar Gamer;Calidad:Certificada TechLab";
        let tags = `${brand}, Alto Rendimiento`;

        if (
          rawTitle.toLowerCase().includes("strix") ||
          rawTitle.toLowerCase().includes("b550")
        ) {
          productDesc =
            "La placa madre ROG Strix B550-F Gaming encarna la esencia pura de Republic of Gamers. Ofrece una entrega de potencia de última generación y una disipación térmica sumamente eficiente optimizada para procesadores Ryzen.";
          specs =
            "Socket:AMD AM4;Chipset:AMD B550;Ranuras RAM:4 x DIMM DDR4 (Máx 128GB);Red:Intel® 2.5Gb Ethernet;Factor:ATX";
          tags = "AMD AM4, PCIe 4.0, DDR4";
        } else if (rawTitle.toLowerCase().includes("4060")) {
          productDesc =
            "Experimenta mundos virtuales sumamente realistas con la GPU MSI RTX 4060 Gaming. Cuenta con el avanzado sistema térmico TWIN FROZR 9 para un flujo de aire y enfriamiento eficientes.";
          specs =
            "Arquitectura:NVIDIA Ada Lovelace;Memoria:8GB GDDR6;Frecuencia OC:2460 MHz;Interfaz:PCI Express 4.0;Salidas:3x DP / 1x HDMI";
          tags = "Ray Tracing, DLSS 3, GDDR6";
        } else if (rawTitle.toLowerCase().includes("ram")) {
          productDesc =
            "Memoria RAM de alto rendimiento diseñada para overclocking estable y fluidez extrema en multitareas y juegos de última generación.";
          specs =
            "Tipo:DDR4;Capacidad:16GB (2x8GB);Velocidad:3200 MHz;Latencia:CL16;Voltaje:1.35V";
          tags = "Dual Channel, RGB, DDR4";
        }

        // 3. Inyectar datos en la interfaz del modal
        if (modalTitle) modalTitle.textContent = `${brand} ${rawTitle}`;
        if (modalPrice) modalPrice.textContent = `S/ ${basePrice}`;
        if (modalBtnPrice) modalBtnPrice.textContent = basePrice;
        if (modalQtyInput) modalQtyInput.value = "1";
        if (modalDescription) modalDescription.textContent = productDesc;

        // Cargar vista inicial de la galería de imágenes
        changeModalImage(null, "principal", currentProductIcon);
        configureThumbnails(currentProductIcon);

        // Inyectar etiquetas (badges)
        if (modalTagsContainer) {
          modalTagsContainer.innerHTML = "";
          tags.split(",").forEach((tag) => {
            const badge = document.createElement("span");
            badge.className = "badge bg-purple-gamer px-3 py-1 me-2 mb-2";
            badge.textContent = tag.trim();
            modalTagsContainer.appendChild(badge);
          });
        }

        // Inyectar filas en la tabla de especificaciones técnicas
        if (modalSpecsTableBody) {
          modalSpecsTableBody.innerHTML = "";
          specs.split(";").forEach((spec) => {
            const parts = spec.split(":");
            if (parts.length === 2) {
              const tr = document.createElement("tr");
              tr.innerHTML = `
                  <th scope="row" class="text-white small py-2" style="width: 40%;">${parts.trim()}</th>
                  <td class="text-secondary small py-2">${parts[4].trim()}</td>
                `;
              modalSpecsTableBody.appendChild(tr);
            }
          });
        }
      }
    }
  });

  // Administración del foco de teclado accesible
  modalFicha.addEventListener("shown.bs.modal", () => {
    if (btnModalAdd) btnModalAdd.focus();
  });
}

// Controladores globales de cantidad interactiva
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

// Confirmar agregación y cerrar modal
window.confirmAddToCart = function () {
  const qty = modalQtyInput ? modalQtyInput.value : 1;
  const name = modalTitle ? modalTitle.textContent : "Producto";
  alert(
    `¡Éxito! Se han reservado temporalmente ${qty} unidades de "${name}" en el carrito.`,
  );

  if (modalFicha) {
    const modalInstance = bootstrap.Modal.getInstance(modalFicha);
    modalInstance?.hide();
  }
};

// Interactividad de la galería de imágenes
window.changeModalImage = function (element, viewType, iconClass) {
  const container = document.getElementById("modalMainImageContainer");
  const tag = document.getElementById("modalImgTag");
  const thumbnails = document.querySelectorAll(".img-thumbnail-gamer");

  let text = "Imagen de Referencia";
  if (viewType === "principal") text = "Imagen de Referencia";
  if (viewType === "secundaria") text = "Foco en Componentes";
  if (viewType === "caja") text = "Empaque y Accesorios";

  if (container && tag) {
    container.innerHTML = `
        <i class="bi ${iconClass} text-info" style="font-size: 8rem; display: block;" aria-hidden="true"></i>
        <span class="fw-bold text-uppercase tracking-wider text-secondary small d-block mt-2" id="modalImgTag">${text}</span>
      `;
  }

  thumbnails.forEach((btn) => btn.classList.remove("active"));
  if (element) {
    element.classList.add("active");
  }
};

// Configurar miniaturas adaptables en tiempo de ejecución
function configureThumbnails(mainIcon) {
  const thumb1 = document.getElementById("thumb1");
  const thumb2 = document.getElementById("thumb2");
  const thumb3 = document.getElementById("thumb3");

  if (thumb1) {
    thumb1.innerHTML = `<i class="bi ${mainIcon} text-info fs-3" aria-hidden="true"></i>`;
    thumb1.onclick = function () {
      changeModalImage(this, "principal", mainIcon);
    };
    thumb1.classList.add("active");
  }
  if (thumb2) {
    let secondaryIcon = "bi-usb-c";
    if (mainIcon === "bi-gpu-card") secondaryIcon = "bi-fan";
    if (mainIcon === "bi-memory") secondaryIcon = "bi-lightning-charge";

    thumb2.innerHTML = `<i class="bi ${secondaryIcon} text-info fs-3" aria-hidden="true"></i>`;
    thumb2.onclick = function () {
      changeModalImage(this, "secundaria", secondaryIcon);
    };
    thumb2.classList.remove("active");
  }
  if (thumb3) {
    thumb3.innerHTML = `<i class="bi bi-box-seam text-info fs-3" aria-hidden="true"></i>`;
    thumb3.onclick = function () {
      changeModalImage(this, "caja", "bi-box-seam");
    };
    thumb3.classList.remove("active");
  }
}


// REGISTRO & SOPORTE =====
const formPerfil = document.querySelector("#formPerfil");
const perfilNombre = document.querySelector("#perfilNombre");
const perfilCorreo = document.querySelector("#perfilCorreo");

// Array porque no hay BD ;-;
const listaUsuarios = [];

if (formPerfil) {
  formPerfil.addEventListener("submit", (event) => {
    event.preventDefault();

    const nombre = perfilNombre.value.trim();
    const correo = perfilCorreo.value.trim().toLowerCase();

    // 1. Validar el @gmail.com
    if (!correo.endsWith("@gmail.com")) {
      perfilCorreo.classList.add("is-invalid");
      return;
    }

    perfilCorreo.classList.remove("is-invalid");

    //Crear nuevo usuario
    const nuevoUsuario = {
      id: Date.now(),
      nombre: nombre,
      correo: correo
    };

    listaUsuarios.push(nuevoUsuario);

    //Ver en consola
    console.log("¡Usuario registrado con éxito!");
    console.log("Lista global de registrados:", listaUsuarios);

    formPerfil.reset();
  });

}