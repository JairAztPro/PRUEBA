"use strict";

document.addEventListener("DOMContentLoaded", () => {
    const btnRestar = document.querySelectorAll('.btn-outline-secondary:first-child');
    const btnSumar = document.querySelectorAll('.btn-outline-secondary:last-child');
    
    // 1. Convertimos la función en global agregando 'window.'
    window.recalcularTotal = () => {
        const inputs = document.querySelectorAll('#listaCarrito input[type="text"]');
        
        const precioPlaca = 899.00;
        const precioRam = 299.00; 

        const cantPlaca = parseInt(inputs[0].value) || 1;
        const cantRam = parseInt(inputs[1].value) || 1;

        let subtotal = (cantPlaca * precioPlaca) + (cantRam * precioRam);
        const costoEnvio = 15.00;

        const botonesUpsell = document.querySelectorAll('.btn-success');
        botonesUpsell.forEach(btn => {
            if(btn.parentElement.innerHTML.includes('55.00')) subtotal += 55.00;
            if(btn.parentElement.innerHTML.includes('120.00')) subtotal += 120.00;
        });

        const totalFinal = subtotal + costoEnvio;

        document.getElementById("resumenSubtotal").innerText = `S/ ${subtotal.toFixed(2)}`;
        
        const totalUI = document.getElementById("resumenTotal");
        totalUI.innerText = `S/ ${totalFinal.toFixed(2)}`;

        totalUI.style.transform = "scale(1.1)";
        setTimeout(() => totalUI.style.transform = "scale(1)", 200);
        
        window.totalAcumulado = totalFinal;
    };

    btnSumar.forEach(btn => {
        btn.addEventListener('click', (e) => {
            if(e.target.innerText === '+') {
                let input = e.target.previousElementSibling;
                input.value = parseInt(input.value) + 1;
                window.recalcularTotal();
            }
        });
    });

    btnRestar.forEach(btn => {
        btn.addEventListener('click', (e) => {
            if(e.target.innerText === '-') {
                let input = e.target.nextElementSibling;
                if(parseInt(input.value) > 1) {
                    input.value = parseInt(input.value) - 1;
                    window.recalcularTotal();
                }
            }
        });
    });

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

    if (btnFinalizarCompra) {
        btnFinalizarCompra.addEventListener("click", (e) => {
            e.preventDefault();
            if(!formEnvio.checkValidity() || !formPago.checkValidity()) {
                alert("Completa todos los datos de envío y pago antes de finalizar.");
                return;
            }
            btnFinalizarCompra.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Procesando...';
            setTimeout(() => {
                alert(`¡Compra procesada con éxito por S/ ${window.totalAcumulado.toFixed(2)}!\nEl recibo ha sido enviado a tu correo.`);
                btnFinalizarCompra.innerHTML = '<i class="bi bi-check-circle-fill me-2"></i> Compra Exitosa';
                btnFinalizarCompra.classList.replace("btn-accent", "btn-success");
            }, 1500);
        });
    }
});

window.agregarUpsell = function(btnElement, precio) {
    // 5. Cambio visual del botón y llamada a la función centralizada
    btnElement.innerHTML = '<i class="bi bi-check2"></i> Agregado';
    btnElement.classList.replace('btn-outline-accent', 'btn-success');
    btnElement.disabled = true;
    
    if(window.recalcularTotal) {
        window.recalcularTotal();
    }
}