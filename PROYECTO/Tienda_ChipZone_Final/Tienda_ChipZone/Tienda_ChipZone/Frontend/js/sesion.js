"use strict";

// Sesión de usuario de ChipZone (simulada en el navegador).
// El backend todavía no tiene usuarios ni autenticación, así que las cuentas se guardan en
// localStorage y la contraseña se guarda como hash SHA-256 (nunca en texto plano).
// Esto sirve para la demostración académica; una tienda real necesita autenticación en el servidor.
const Sesion = (() => {
    const CLAVE_USUARIOS = "chipzone_usuarios";
    const CLAVE_SESION = "chipzone_sesion";

    function leer(almacen, clave, defecto) {
        try {
            return JSON.parse(almacen.getItem(clave) ?? "null") ?? defecto;
        } catch {
            return defecto;
        }
    }

    async function hash(texto) {
        const bytes = new TextEncoder().encode(texto);
        const resumen = await crypto.subtle.digest("SHA-256", bytes);
        return Array.from(new Uint8Array(resumen))
            .map((b) => b.toString(16).padStart(2, "0"))
            .join("");
    }

    const normalizarCorreo = (correo) => String(correo ?? "").trim().toLowerCase();

    function actual() {
        return leer(sessionStorage, CLAVE_SESION, null) ?? leer(localStorage, CLAVE_SESION, null);
    }

    function guardarSesion(usuario, recordar) {
        const datos = JSON.stringify({ nombre: usuario.nombre, correo: usuario.correo });
        sessionStorage.removeItem(CLAVE_SESION);
        localStorage.removeItem(CLAVE_SESION);
        (recordar ? localStorage : sessionStorage).setItem(CLAVE_SESION, datos);
    }

    async function registrar({ nombre, correo, pass }) {
        const usuarios = leer(localStorage, CLAVE_USUARIOS, []);
        const email = normalizarCorreo(correo);
        if (usuarios.some((u) => u.correo === email)) {
            return { ok: false, error: "Ya existe una cuenta con ese correo." };
        }
        const usuario = { nombre: nombre.trim(), correo: email, hash: await hash(pass) };
        usuarios.push(usuario);
        localStorage.setItem(CLAVE_USUARIOS, JSON.stringify(usuarios));
        guardarSesion(usuario, false);
        return { ok: true, usuario };
    }

    async function iniciar(correo, pass, recordar) {
        const email = normalizarCorreo(correo);
        const usuario = leer(localStorage, CLAVE_USUARIOS, []).find((u) => u.correo === email);
        // Mismo mensaje si falla el correo o la contraseña: no revela qué correos existen
        if (!usuario || usuario.hash !== (await hash(pass))) {
            return { ok: false, error: "Correo o contraseña incorrectos. Si no tienes cuenta, crea una." };
        }
        guardarSesion(usuario, recordar);
        return { ok: true, usuario };
    }

    function cerrar() {
        sessionStorage.removeItem(CLAVE_SESION);
        localStorage.removeItem(CLAVE_SESION);
    }

    return { actual, registrar, iniciar, cerrar };
})();

(function conectarInterfaz() {
    const modal = document.getElementById("loginModal");
    const botonIniciar = document.querySelector('[data-bs-target="#loginModal"]');

    // Cabecera: "Iniciar Sesión" si no hay sesión; saludo y "Cerrar sesión" si la hay
    function pintarCabecera() {
        if (!botonIniciar) return;
        document.getElementById("zonaSesion")?.remove();
        const usuario = Sesion.actual();
        botonIniciar.classList.toggle("d-none", Boolean(usuario));
        if (!usuario) return;

        const zona = document.createElement("div");
        zona.id = "zonaSesion";
        zona.className = "d-flex align-items-center gap-2";

        const saludo = document.createElement("span");
        saludo.className = "text-light";
        saludo.textContent = `Hola, ${usuario.nombre}`; // textContent: el nombre nunca se interpreta como HTML

        const salir = document.createElement("button");
        salir.type = "button";
        salir.className = "btn btn-outline-light btn-sm";
        salir.textContent = "Cerrar sesión";
        salir.addEventListener("click", () => {
            Sesion.cerrar();
            pintarCabecera();
        });

        zona.append(saludo, salir);
        botonIniciar.after(zona);
    }

    function mostrarError(formulario, texto) {
        let aviso = formulario.querySelector("[data-aviso]");
        if (!aviso) {
            aviso = document.createElement("div");
            aviso.setAttribute("data-aviso", "");
            aviso.setAttribute("role", "alert");
            aviso.className = "alert alert-danger py-2 small";
            formulario.prepend(aviso);
        }
        aviso.textContent = texto;
    }

    const limpiarError = (formulario) => formulario.querySelector("[data-aviso]")?.remove();

    function terminar(formulario) {
        formulario.reset();
        formulario.classList.remove("was-validated");
        if (modal && window.bootstrap) bootstrap.Modal.getInstance(modal)?.hide();
        pintarCabecera();
    }

    const formLogin = document.getElementById("formLogin");
    formLogin?.addEventListener("submit", async (e) => {
        e.preventDefault();
        limpiarError(formLogin);
        formLogin.classList.add("was-validated");
        if (!formLogin.checkValidity()) {
            formLogin.querySelector(":invalid")?.focus();
            return;
        }
        const resultado = await Sesion.iniciar(
            document.getElementById("loginEmail").value,
            document.getElementById("loginPass").value,
            document.getElementById("rememberMe").checked);
        if (!resultado.ok) {
            mostrarError(formLogin, resultado.error);
            return;
        }
        terminar(formLogin);
    });

    const formRegistro = document.getElementById("formRegistro");
    const regPass = document.getElementById("regPass");
    const regPass2 = document.getElementById("regPass2");
    const validarCoincidencia = () => regPass2?.setCustomValidity(
        regPass.value === regPass2.value ? "" : "Las contraseñas no coinciden.");
    regPass?.addEventListener("input", validarCoincidencia);
    regPass2?.addEventListener("input", validarCoincidencia);

    formRegistro?.addEventListener("submit", async (e) => {
        e.preventDefault();
        limpiarError(formRegistro);
        validarCoincidencia();
        formRegistro.classList.add("was-validated");
        if (!formRegistro.checkValidity()) {
            formRegistro.querySelector(":invalid")?.focus();
            return;
        }
        const nombre = document.getElementById("regNombres").value;
        const resultado = await Sesion.registrar({
            nombre,
            correo: document.getElementById("regCorreo").value,
            pass: regPass.value,
        });
        if (!resultado.ok) {
            mostrarError(formRegistro, resultado.error);
            return;
        }
        terminar(formRegistro); // la cuenta nueva ya queda con la sesión iniciada
    });

    pintarCabecera();
})();