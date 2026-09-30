"use strict";

const API_URL = "http://localhost:8080";

async function obtenerProductos(q = "", categoria = "") {
  const parametros = new URLSearchParams({ q, categoria });
  const respuesta = await fetch(`${API_URL}/api/v1/productos?${parametros}`, {
    headers: { Accept: "application/json" }
  });
  if (!respuesta.ok) {
    throw new Error(`HTTP ${respuesta.status}`);
  }
  return respuesta.json();
}

// Precio final = precio de lista menos el porcentaje de descuento (si tiene).
// Ejemplo: precio 899 con 20% de descuento -> 719.
function precioFinal(producto) {
  const descuento = Number(producto.descuento) || 0;
  if (descuento <= 0) {
    return producto.precio;
  }
  return Math.round(producto.precio * (100 - descuento) / 100);
}