document.addEventListener('DOMContentLoaded', () => {
  const anioSpan = document.getElementById('anioActual');
  if (anioSpan) anioSpan.textContent = new Date().getFullYear();

  // uso de array porque no hay BD ;-;
  const usuariosRegistrados = [];

  const formPerfil = document.getElementById('formPerfil');
  if (formPerfil) {
    formPerfil.addEventListener('submit', (e) => {
      e.preventDefault();

      const inputNombre = document.getElementById('perfilNombre');
      const inputCorreo = document.getElementById('perfilCorreo');
      const inputPass = document.getElementById('perfilPass');

      // validaciones + restricciones
      const nombreValido = inputNombre.value.trim() !== '';
      const correoValido = inputCorreo.value.trim().toLowerCase().endsWith('@gmail.com');
      const passValido = inputPass.value.trim().length >= 6;

      inputNombre.classList.toggle('is-invalid', !nombreValido);
      inputCorreo.classList.toggle('is-invalid', !correoValido);
      inputPass.classList.toggle('is-invalid', !passValido);

      if (nombreValido && correoValido && passValido) {
        // toma de datos
        const nuevoUsuario = {
          id: Date.now(),
          usuario: inputNombre.value.trim(),
          correo: inputCorreo.value.trim().toLowerCase(),
          pass: inputPass.value.trim(),
          fechaRegistro: new Date().toLocaleString()
        };

        // subida al array
        usuariosRegistrados.push(nuevoUsuario);

        // vista en consola
        console.log('Usuario registrado exitosamente');
        console.table(usuariosRegistrados);

        alert(`¡Registro exitoso! Bienvenido, ${nuevoUsuario.usuario}.`);

        // limpiar form
        formPerfil.reset();
        inputNombre.classList.remove('is-invalid');
        inputCorreo.classList.remove('is-invalid');
        inputPass.classList.remove('is-invalid');
      }
    });
  }
});