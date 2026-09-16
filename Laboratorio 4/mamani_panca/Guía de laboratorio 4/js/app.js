"use strict";

/* ---------------------------------------------------------------------
 * 1. Año dinámico en el pie de página (RF-10)
 * ------------------------------------------------------------------- */
const anioActual = document.querySelector("#anioActual");
if (anioActual) {
  anioActual.textContent = new Date().getFullYear();
}

/* ---------------------------------------------------------------------
 * 2. Modal reutilizable: transferir la ruta elegida (RF-06)
 * ------------------------------------------------------------------- */
const modalRegistro = document.querySelector("#modalRegistro");
const campoCurso = document.querySelector("#curso");

if (modalRegistro && campoCurso) {
  modalRegistro.addEventListener("show.bs.modal", (event) => {
    const activador = event.relatedTarget;
    const curso = activador?.dataset.curso ?? "";
    campoCurso.value = curso;
  });

  /* RF-09: limpiar el formulario por completo al cerrar el modal,
     para que no queden estados was-validated ni mensajes obsoletos. */
  modalRegistro.addEventListener("hidden.bs.modal", () => {
    const form = document.querySelector("#formRegistro");
    const estado = document.querySelector("#estadoFormulario");

    if (form) {
      form.reset();
      form.classList.remove("was-validated");
    }
    if (estado) {
      estado.textContent = "";
      estado.classList.add("d-none");
    }
  });
}

/* ---------------------------------------------------------------------
 * 3. Validación del formulario de registro (RF-07, RF-08)
 * ------------------------------------------------------------------- */
const form = document.querySelector("#formRegistro");
const estado = document.querySelector("#estadoFormulario");

if (form && estado) {
  form.addEventListener("submit", (event) => {
    event.preventDefault();
    form.classList.add("was-validated");

    if (!form.checkValidity()) {
      form.querySelector(":invalid")?.focus();
      estado.textContent = "";
      estado.classList.add("d-none");
      return;
    }

    /* No se envía a un servidor: es una simulación explícita del registro. */
    estado.textContent = "Registro de demostración completado. Gracias por tu interés.";
    estado.classList.remove("d-none");
  });
}
