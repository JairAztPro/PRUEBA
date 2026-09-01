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

"use strict";

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
            .filter(m => m.checked)
            .map(m => m.value);

        const criterio = selectOrden ? selectOrden.value : "";
        const items = [...productos];

        // 1. Filtrar
        items.forEach(producto => {
            const marca = producto.dataset.marca;
            const precio = Number(producto.dataset.precio);
            const disponible = producto.dataset.stock === "true";

            const marcaOK = !marcasActivas.length || marcasActivas.includes(marca);
            const precioOK = precio <= Number(rango.value);
            const stockOK = !stock.checked || disponible;

            producto.classList.toggle(
                "d-none",
                !(marcaOK && precioOK && stockOK)
            );
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
        items.forEach(item => contenedorProductos.appendChild(item));
    }

    // Eventos
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
        stock.checked = false;
        rango.value = 1500;
        valor.textContent = "S/ 1500";
        if (selectOrden) selectOrden.selectedIndex = 0;

        aplicarFiltrosYOrden();
    });
});