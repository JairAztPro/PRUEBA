"use strict";

/**
 * Array de datos de productos, pensado para ser el archivo COMPARTIDO
 * entre las 6 vistas del proyecto (cada integrante lo copia igual).
 * Cuando exista un backend, este archivo se reemplaza por una petición
 * al servidor; por ahora simula el catálogo.
 */
const PRODUCTOS = [
  {
    id: "p001",
    nombre: "Laptop Gamer XR-15",
    categoria: "gaming",
    precio: 2499,
    precioAnterior: 2899,
    calificacion: 4.5,
    imagen: "https://images.unsplash.com/photo-1603302576837-37561b2e2302?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["mas-vendidos", "ofertas"],
  },
  {
    id: "p002",
    nombre: "Celular NovaPhone 14",
    categoria: "celulares",
    precio: 1599,
    precioAnterior: null,
    calificacion: 4.2,
    imagen: "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["mas-vendidos", "nuevos"],
  },
  {
    id: "p003",
    nombre: "Teclado mecánico RGB",
    categoria: "accesorios",
    precio: 249,
    precioAnterior: 299,
    calificacion: 4.7,
    imagen: "https://images.unsplash.com/photo-1595225476474-89badb31c15a?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["ofertas"],
  },
  {
    id: "p004",
    nombre: "Laptop Ultra Slim 14\"",
    categoria: "laptops",
    precio: 3299,
    precioAnterior: null,
    calificacion: 4.4,
    imagen: "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["nuevos"],
  },
  {
    id: "p005",
    nombre: "Audífonos gaming ClearSound",
    categoria: "accesorios",
    precio: 189,
    precioAnterior: null,
    calificacion: 4.3,
    imagen: "https://images.unsplash.com/photo-1599669454699-248893623440?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["mas-vendidos"],
  },
  {
    id: "p006",
    nombre: "Monitor curvo 27\" 144Hz",
    categoria: "gaming",
    precio: 899,
    precioAnterior: 1099,
    calificacion: 4.6,
    imagen: "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=600&q=80",
    etiquetas: ["nuevos", "ofertas"],
  },
];