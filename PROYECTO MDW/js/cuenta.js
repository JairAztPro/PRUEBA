document.addEventListener('DOMContentLoaded', () => {
  const anioSpan = document.getElementById('anioActual');
  if (anioSpan) anioSpan.textContent = new Date().getFullYear();

  // array porque no hay BD ;-;
  const usuariosRegistrados = [];

  const formPerfil = document.getElementById('formPerfil');
  if (formPerfil) {
    formPerfil.addEventListener('submit', (e) => {
      e.preventDefault();

      const inputNombre = document.getElementById('perfilNombre');
      const inputCorreo = document.getElementById('perfilCorreo');

      const nombreValido = inputNombre.value.trim() !== '';
      const correoValido = inputCorreo.value.trim().toLowerCase().endsWith('@gmail.com');

      inputNombre.classList.toggle('is-invalid', !nombreValido);
      inputCorreo.classList.toggle('is-invalid', !correoValido);

      if (nombreValido && correoValido) {
        // creacion de nuevo usuario
        const nuevoUsuario = {
          id: Date.now(),
          usuario: inputNombre.value.trim(),
          correo: inputCorreo.value.trim().toLowerCase(),
          fechaRegistro: new Date().toLocaleString()
        };

        usuariosRegistrados.push(nuevoUsuario);

        // vista en consola
        console.log('Nuevo usuario registrado');
        console.table(usuariosRegistrados);

        alert(`¡Registro exitoso! Bienvenido, ${nuevoUsuario.usuario}.`);

        // limpieza del form
        formPerfil.reset();
        inputNombre.classList.remove('is-invalid');
        inputCorreo.classList.remove('is-invalid');
      }
    });
  }
});