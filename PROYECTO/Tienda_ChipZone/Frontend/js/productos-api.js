"use strict";

// Direccion del backend Spring Boot (ejecutar el backend en el puerto 8080).
const API_URL = "http://localhost:8080";

const formulario = document.querySelector("#busqueda");
const consulta = document.querySelector("#consulta");
const categoria = document.querySelector("#categoria");
const boton = document.querySelector("#buscar");
const lista = document.querySelector("#productos");
const estado = document.querySelector("#estado");

function mostrarProductos(productos) {
  lista.replaceChildren();
  for (const producto of productos) {
    const item = document.createElement("li");
    item.className = "list-group-item";

    const titulo = document.createElement("h2");
    titulo.className = "h5";
    titulo.textContent = producto.nombre;

    const detalle = document.createElement("p");
    detalle.className = "mb-0";
    detalle.textContent =
      `${producto.marca.toUpperCase()} · ${producto.categoria} · S/ ${producto.precio}` +
      (producto.stock ? "" : " · Agotado");

    item.append(titulo, detalle);
    lista.append(item);
  }
}

async function cargarProductos() {
  boton.disabled = true;
  formulario.setAttribute("aria-busy", "true");
  estado.textContent = "Consultando productos...";
  lista.replaceChildren();

  try {
    const parametros = new URLSearchParams({
      q: consulta.value,
      categoria: categoria.value
    });
    const respuesta = await fetch(`${API_URL}/api/v1/productos?${parametros}`, {
      headers: { Accept: "application/json" }
    });
    if (!respuesta.ok) {
      throw new Error(`HTTP ${respuesta.status}`);
    }
    const productos = await respuesta.json();
    if (!Array.isArray(productos)) {
      throw new Error("Formato de respuesta inesperado");
    }
    mostrarProductos(productos);
    estado.textContent = productos.length
      ? `${productos.length} productos encontrados.`
      : "No hay productos que coincidan.";
  } catch (error) {
    estado.textContent = "No se pudo cargar el catálogo. ¿Está encendido el backend?";
    console.error("Fallo al consultar los productos", error);
  } finally {
    boton.disabled = false;
    formulario.removeAttribute("aria-busy");
  }
}

formulario.addEventListener("submit", (evento) => {
  evento.preventDefault();
  if (!boton.disabled) cargarProductos();
});

cargarProductos();
