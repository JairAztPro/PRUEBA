"use strict";

// Carrito compartido por todas las paginas. Guarda solo {id, cantidad} en localStorage;
// nombres y precios siempre se leen de la API, asi nunca quedan desactualizados.
const Carrito = (() => {
  const CLAVE = "chipzone_carrito";
  const MAX_POR_PRODUCTO = 10;

  function items() {
    try {
      const datos = JSON.parse(localStorage.getItem(CLAVE) ?? "[]");
      if (!Array.isArray(datos)) return [];
      return datos
        .map((i) => ({ id: Number(i?.id), cantidad: Math.trunc(Number(i?.cantidad)) }))
        .filter((i) => Number.isInteger(i.id) && i.id > 0 && i.cantidad >= 1)
        .map((i) => ({ id: i.id, cantidad: Math.min(i.cantidad, MAX_POR_PRODUCTO) }));
    } catch (error) {
      return [];
    }
  }

  function guardar(lista) {
    try {
      localStorage.setItem(CLAVE, JSON.stringify(lista));
    } catch (error) {
      console.error("No se pudo guardar el carrito", error);
    }
    actualizarBadge();
  }

  function cantidadTotal() {
    return items().reduce((suma, i) => suma + i.cantidad, 0);
  }

  /** Suma unidades del producto. Devuelve {cantidad, limitado}. */
  function agregar(id, cantidad = 1) {
    const lista = items();
    const existente = lista.find((i) => i.id === id);
    const deseada = (existente ? existente.cantidad : 0) + Math.max(1, Math.trunc(cantidad));
    const final = Math.min(deseada, MAX_POR_PRODUCTO);
    if (existente) {
      existente.cantidad = final;
    } else {
      lista.push({ id, cantidad: final });
    }
    guardar(lista);
    return { cantidad: final, limitado: deseada > MAX_POR_PRODUCTO };
  }

  function cambiarCantidad(id, cantidad) {
    if (cantidad < 1) {
      quitar(id);
      return;
    }
    const lista = items();
    const item = lista.find((i) => i.id === id);
    if (item) {
      item.cantidad = Math.min(Math.trunc(cantidad), MAX_POR_PRODUCTO);
      guardar(lista);
    }
  }

  function quitar(id) {
    guardar(items().filter((i) => i.id !== id));
  }

  function vaciar() {
    guardar([]);
  }

  // Actualiza el numerito del icono del carrito en la barra de navegacion
  function actualizarBadge() {
    const total = cantidadTotal();
    document.querySelectorAll("[data-carrito-badge]").forEach((badge) => {
      badge.textContent = String(total);
      badge.classList.toggle("d-none", total === 0);
      badge.closest("a, button")?.setAttribute("aria-label", `Carrito de compras, ${total} producto(s)`);
    });
  }

  document.addEventListener("DOMContentLoaded", actualizarBadge);
  // Si el carrito cambia en otra pestana, este numerito se sincroniza
  window.addEventListener("storage", (evento) => {
    if (evento.key === CLAVE) actualizarBadge();
  });

  return { items, cantidadTotal, agregar, cambiarCantidad, quitar, vaciar, MAX_POR_PRODUCTO };
})();
