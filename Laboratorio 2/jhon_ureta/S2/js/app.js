"use strict";

document.addEventListener("DOMContentLoaded", () => {
  const yearElement = document.querySelector("[data-current-year]");
  const statusElement = document.querySelector("#estado-registro");
  const buttonsTramite = document.querySelectorAll(".btn-tramite");

  // Actualizar año del footer
  if (yearElement) {
    yearElement.textContent = String(new Date().getFullYear());
  }

  if (!statusElement) {
    return;
  }

  // Comportamiento de los botones de la tabla
  buttonsTramite.forEach((button) => {
    button.addEventListener("click", () => {
      // Capturar el nombre del trámite a través del atributo data
      const tramiteName = button.dataset.tramite ?? "el trámite seleccionado";
      
      // Actualizar y mostrar el mensaje en el DOM
      statusElement.textContent = `Has iniciado la solicitud para: ${tramiteName}. Este es un proceso simulado.`;
      statusElement.classList.remove("d-none");
    });
  });
});