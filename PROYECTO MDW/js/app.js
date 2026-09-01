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