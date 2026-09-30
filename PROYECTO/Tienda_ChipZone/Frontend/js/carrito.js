"use strict";

document.addEventListener("DOMContentLoaded", async () => {
    const COSTO_ENVIO = 15;
    const lista = document.getElementById("listaCarrito");
    const aviso = document.getElementById("avisoCarrito");
    const btnContinuarEnvio = document.getElementById("btnContinuarEnvio");
    const btnPagar = document.getElementById("btnFinalizarCompra");
    const extras = []; // precios de las ventas cruzadas (no son productos de la API)
    let catalogo = new Map();
    let cargado = false;

    const soles = (n) => `S/ ${n.toFixed(2)}`;
    const esc = (texto) => String(texto ?? "")
        .replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;").replace(/'/g, "&#39;");

    // 1. CARRITO REAL: productos y precios vienen de la API, cantidades de localStorage
    function filaProducto(p, cantidad) {
        const final = precioFinal(p);
        const anterior = p.descuento
            ? `<small class="text-decoration-line-through text-secondary d-block">${soles(p.precio * cantidad)}</small>`
            : "";
        return `
          <li class="list-group-item bg-transparent text-white d-flex justify-content-between align-items-center py-3 flex-wrap gap-3">
            <div class="d-flex align-items-center gap-3">
              <div class="bg-dark rounded border border-secondary overflow-hidden" style="width: 64px; height: 64px;">
                <img src="${esc(p.imagenes[0])}" alt="" class="w-100 h-100 object-fit-cover">
              </div>
              <div>
                <h6 class="mb-0">${esc(p.nombreCompleto)}</h6>
                <small class="text-secondary">${esc(p.marca.toUpperCase())} · S/ ${final} c/u</small>
              </div>
            </div>
            <div class="d-flex align-items-center gap-4">
              <div class="input-group input-group-sm" style="width: 110px;">
                <button class="btn btn-outline-secondary" type="button" data-accion="restar" data-id="${p.id}" aria-label="Quitar una unidad de ${esc(p.nombre)}">-</button>
                <input type="text" class="form-control text-center bg-dark text-white border-secondary" value="${cantidad}" readonly aria-label="Cantidad">
                <button class="btn btn-outline-secondary" type="button" data-accion="sumar" data-id="${p.id}" aria-label="Agregar una unidad de ${esc(p.nombre)}" ${cantidad >= Carrito.MAX_POR_PRODUCTO ? "disabled" : ""}>+</button>
              </div>
              <span class="fw-bold text-end" style="min-width: 90px;">${anterior}${soles(final * cantidad)}</span>
              <button class="btn btn-link text-danger p-0" type="button" data-accion="quitar" data-id="${p.id}" aria-label="Quitar ${esc(p.nombre)} del carrito"><i class="bi bi-trash3"></i></button>
            </div>
          </li>`;
    }

    function recalcularTotal() {
        let bruto = 0;
        let ahorro = 0;
        let unidades = 0;
        for (const { id, cantidad } of Carrito.items()) {
            const p = catalogo.get(id);
            if (!p) continue;
            bruto += p.precio * cantidad;
            ahorro += (p.precio - precioFinal(p)) * cantidad;
            unidades += cantidad;
        }
        const hayProductos = unidades > 0;
        const subtotal = bruto + (hayProductos ? extras.reduce((a, b) => a + b, 0) : 0);
        const envio = hayProductos ? COSTO_ENVIO : 0;
        const total = subtotal - ahorro + envio;

        document.getElementById("resumenCantidad").textContent =
            `Subtotal (${unidades} producto${unidades === 1 ? "" : "s"})`;
        document.getElementById("resumenSubtotal").textContent = soles(subtotal);
        document.getElementById("resumenDescuentos").textContent = `- ${soles(ahorro)}`;
        document.getElementById("resumenEnvio").textContent = soles(envio);

        const totalUI = document.getElementById("resumenTotal");
        totalUI.textContent = soles(total);
        totalUI.style.transform = "scale(1.1)";
        setTimeout(() => totalUI.style.transform = "scale(1)", 200);

        window.totalAcumulado = total;
        if (btnPagar) btnPagar.disabled = !hayProductos || !cargado;
        if (btnContinuarEnvio) btnContinuarEnvio.disabled = !hayProductos;
    }
    window.recalcularTotal = recalcularTotal;

    function renderizar() {
        const enCarrito = Carrito.items().filter((i) => catalogo.has(i.id));
        lista.innerHTML = enCarrito.length > 0
            ? enCarrito.map((i) => filaProducto(catalogo.get(i.id), i.cantidad)).join("")
            : `<li class="list-group-item bg-transparent text-secondary text-center py-4">
                 ${cargado ? 'Tu carrito está vacío. <a href="catalogo.html" class="text-accent">Ir al catálogo</a>' : "Cargando carrito..."}
               </li>`;
        recalcularTotal();
    }

    lista.addEventListener("click", (evento) => {
        const boton = evento.target.closest("[data-accion]");
        if (!boton) return;
        const id = Number(boton.dataset.id);
        const actual = Carrito.items().find((i) => i.id === id)?.cantidad ?? 0;
        if (boton.dataset.accion === "sumar") Carrito.cambiarCantidad(id, actual + 1);
        if (boton.dataset.accion === "restar") Carrito.cambiarCantidad(id, actual - 1);
        if (boton.dataset.accion === "quitar") Carrito.quitar(id);
        renderizar();
    });

    // Ventas cruzadas (Hub, cable): suman al total mientras haya productos en el carrito
    window.agregarUpsell = function (btnElement, precio) {
        btnElement.innerHTML = '<i class="bi bi-check2"></i> Agregado';
        btnElement.classList.replace("btn-outline-accent", "btn-success");
        btnElement.disabled = true;
        extras.push(precio);
        recalcularTotal();
    };

    // 2. CAMBIO DE ESTILOS EN TABS DE MODAL
    const tabs = document.querySelectorAll('#authTabs .nav-link');
    tabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            tabs.forEach(t => {
                t.classList.remove('text-white', 'border-secondary', 'active');
                t.classList.add('text-secondary', 'border-0');
            });
            e.target.classList.remove('text-secondary', 'border-0');
            e.target.classList.add('text-white', 'border-secondary', 'active');
        });
    });

    // 3. VALIDACIÓN ESTRICTA
    const validarFormulario = (formElement, successMessage) => {
        if (!formElement) return;
        formElement.addEventListener("submit", (e) => {
            e.preventDefault();
            formElement.classList.add("was-validated");
            if (!formElement.checkValidity()) {
                formElement.querySelector(":invalid")?.focus();
                return;
            }
            alert(successMessage);
            formElement.reset();
            formElement.classList.remove("was-validated");
        });
    };

    validarFormulario(document.getElementById("formLogin"), "¡Inicio de sesión exitoso!");
    validarFormulario(document.getElementById("formRegistro"), "¡Cuenta creada correctamente!");

    // 4. FLUJO DE CHECKOUT
    const formEnvio = document.getElementById("formEnvio");
    const btnValidarEnvio = document.getElementById("btnValidarEnvio");
    const btnAccordionPago = document.getElementById("btnAccordionPago");
    const btnFinalizarCompra = document.getElementById("btnFinalizarCompra");
    const formPago = document.getElementById("formPago");

    if (btnValidarEnvio) {
        btnValidarEnvio.addEventListener("click", (e) => {
            formEnvio.classList.add("was-validated");
            if (formEnvio.checkValidity()) {
                btnAccordionPago.removeAttribute("disabled");
                new bootstrap.Collapse(document.getElementById('paso3'), { toggle: true });
            } else {
                formEnvio.querySelector(":invalid")?.focus();
            }
        });
    }

    // Tarjeta o Yape/Plin: con Yape se ocultan y se desactivan los campos de tarjeta
    // (los campos desactivados no cuentan en la validacion del formulario)
    const datosTarjeta = document.getElementById("datosTarjeta");
    const datosYape = document.getElementById("datosYape");
    function actualizarMetodoPago() {
        const conTarjeta = document.getElementById("pagoTarjeta").checked;
        datosTarjeta.classList.toggle("d-none", !conTarjeta);
        datosYape.classList.toggle("d-none", conTarjeta);
        datosTarjeta.querySelectorAll("input").forEach((campo) => { campo.disabled = !conTarjeta; });
    }
    document.querySelectorAll('input[name="tipoPago"]').forEach((radio) => {
        radio.addEventListener("change", actualizarMetodoPago);
    });
    actualizarMetodoPago();

    if (btnFinalizarCompra) {
        btnFinalizarCompra.addEventListener("click", (e) => {
            e.preventDefault();
            // Primero el envio (paso 2), luego el pago (paso 3); se marca lo que falta
            formEnvio.classList.add("was-validated");
            if (!formEnvio.checkValidity()) {
                new bootstrap.Collapse(document.getElementById("paso2"), { toggle: false }).show();
                formEnvio.querySelector(":invalid")?.focus();
                alert("Completa los datos de envío para continuar.");
                return;
            }
            btnAccordionPago.removeAttribute("disabled");
            formPago.classList.add("was-validated");
            if (!formPago.checkValidity()) {
                new bootstrap.Collapse(document.getElementById("paso3"), { toggle: false }).show();
                formPago.querySelector(":invalid")?.focus();
                alert("Completa los datos de pago y acepta los términos para finalizar.");
                return;
            }
            btnFinalizarCompra.disabled = true; // evita pagar dos veces con doble clic
            btnFinalizarCompra.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Procesando...';
            setTimeout(() => {
                alert(`¡Compra procesada con éxito por S/ ${window.totalAcumulado.toFixed(2)}!\nEl recibo ha sido enviado a tu correo.`);
                btnFinalizarCompra.innerHTML = '<i class="bi bi-check-circle-fill me-2"></i> Compra Exitosa';
                btnFinalizarCompra.classList.replace("btn-accent", "btn-success");
                Carrito.vaciar();
                renderizar();
            }, 1500);
        });
    }

    // Carga inicial: trae el catalogo de la API y quita lo que ya no existe o se agoto
    if (btnPagar) btnPagar.disabled = true;
    if (btnContinuarEnvio) btnContinuarEnvio.disabled = true;
    renderizar();
    try {
        const productos = await obtenerProductos();
        catalogo = new Map(productos.map((p) => [p.id, p]));
        const invalidos = Carrito.items().filter((i) => !catalogo.get(i.id)?.stock);
        invalidos.forEach((i) => Carrito.quitar(i.id));
        if (aviso && invalidos.length > 0) {
            aviso.textContent = `Se quitaron ${invalidos.length} producto(s) que ya no están disponibles.`;
            aviso.classList.remove("d-none");
        }
        cargado = true;
        renderizar();
    } catch (error) {
        console.error(error);
        lista.innerHTML = `<li class="list-group-item bg-transparent"><div class="alert alert-danger mb-0">No se pudo cargar el carrito. ¿Está encendido el backend?</div></li>`;
    }
});