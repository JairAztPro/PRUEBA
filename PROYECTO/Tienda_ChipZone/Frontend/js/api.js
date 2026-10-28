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

// ---------------------------------------------------------------------------
// Imagenes de producto (carpeta Frontend/img)
// Cada producto del catalogo que tiene fotos en img/ las usa en tarjetas,
// carrito y ficha tecnica (carrusel). Las rutas son relativas a html/.
// El producto se identifica por su nombre (campo "nombre" de la API).
// ---------------------------------------------------------------------------
const CARPETA_IMG = "../img/";

const IMAGENES_LOCALES = {
  "ROG Strix B550-F": [
    "1-rog-strix-b550-f.png", "2-rog-strix-b550-f.png", "3-rog-strix-b550-f.png"],
  "Ryzen 7 5800X3D": [
    "1-procesador-amd-ryzen-7-5800x3d.png", "2-procesador-amd-ryzen-7-5800x3d.png", "3-procesador-amd-ryzen-7-5800x3d.png"],
  "Teclado Vigor GK30": [
    "1-teclado-mecanico-msi-vigor-gk30.webp", "2-teclado-mecanico-msi-vigor-gk30.webp", "3-teclado-mecanico-msi-vigor-gk30.webp"],
  "Monitor TUF 27\" 170Hz": [
    "1-monitor-asus-tuf-gaming-27-170hz.png", "2-monitor-asus-tuf-gaming-27-170hz.png", "3-monitor-asus-tuf-gaming-27-170hz.png"],
  "Fuente 750W Gold": [
    "1-fuente-de-poder-msi-750w-gold.png", "2-fuente-de-poder-msi-750w-gold.png", "3-fuente-de-poder-msi-750w-gold.png"],
  // La carpeta img solo trae 2 fotos de esta silla (falta 3-silla-gamer-asus-rog-chariot.png)
  "Silla ROG Chariot": [
    "1-silla-gamer-asus-rog-chariot.png", "2-silla-gamer-asus-rog-chariot.png"],
  "Cooler Líquido MAG 240": [
    "1-refrigeracion-liquida-msi-mag-240.png", "2-refrigeracion-liquida-msi-mag-240.png", "3-refrigeracion-liquida-msi-mag-240.png"],
  "Laptop Academic": [
    "1-laptop-lenovo-v15-g6-itn-15-6.png", "2-laptop-lenovo-v15-g6-itn-15-6.png", "3-laptop-lenovo-v15-g6-itn-15-6.png"],
  "Audífonos Aura": [
    "1-audifonos-bluetooth-true-wireless-jbl.png", "2-audifonos-bluetooth-true-wireless-jbl.png", "3-audifonos-bluetooth-true-wireless-jbl.png"],
  "Gabinete Cristal": [
    "1-gabinete-antryx-xtreme-nc-253.png", "2-gabinete-antryx-xtreme-nc-253.png", "3-gabinete-antryx-xtreme-nc-253.png"]
};

// Icono de reemplazo cuando un producto no tiene ninguna imagen disponible
const ICONOS_CATEGORIA = {
  laptops: "bi-laptop",
  celulares: "bi-phone",
  accesorios: "bi-headset",
  gaming: "bi-motherboard"
};

/**
 * Imagenes que debe mostrar un producto: primero las de la carpeta img/;
 * si no tiene, las que vengan de la API (se descartan rutas /images/... que
 * no existen en el frontend). Puede devolver [] (se usa un icono).
 */
function imagenesProducto(producto) {
  const locales = IMAGENES_LOCALES[producto.nombre];
  if (locales) {
    return locales.map((archivo) => CARPETA_IMG + archivo);
  }
  return (producto.imagenes ?? []).filter((url) => /^https?:\/\//i.test(url));
}

function iconoCategoria(producto) {
  return ICONOS_CATEGORIA[producto.categoria] ?? "bi-box-seam";
}

// Si una imagen falla al cargar, se reemplaza por el icono de su categoria
document.addEventListener("error", (evento) => {
  const img = evento.target;
  if (!(img instanceof HTMLImageElement) || !img.dataset.icono) return;
  const icono = document.createElement("i");
  icono.className = `bi ${img.dataset.icono} imagen-reemplazo`;
  icono.setAttribute("aria-hidden", "true");
  img.replaceWith(icono);
}, true);

