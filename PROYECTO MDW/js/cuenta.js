document.addEventListener('DOMContentLoaded', () => {
    // Año dinámico footer
    document.getElementById('anioActual').textContent = new Date().getFullYear();

    // Validación rápida del formulario
    const formPerfil = document.getElementById('formPerfil');

    formPerfil.addEventListener('submit', (e) => {
        e.preventDefault();

        const inputNombre = document.getElementById('perfilNombre');
        const inputCorreo = document.getElementById('perfilCorreo');

        const nombreValido = inputNombre.value.trim() !== '';
        const correoValido = inputCorreo.value.trim().toLowerCase().endsWith('@gmail.com');

        inputNombre.classList.toggle('is-invalid', !nombreValido);
        inputCorreo.classList.toggle('is-invalid', !correoValido);

        if (nombreValido && correoValido) {
            alert('¡Registro exitoso!');
            formPerfil.reset();
            inputNombre.classList.remove('is-invalid');
            inputCorreo.classList.remove('is-invalid');
        }
    });
});