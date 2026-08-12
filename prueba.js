const username = prompt("Por favor, ingresa tu nombre: ");
alert("Hola " + username + " ¡Bienvenido al ejemplo de JS!");

document.getElementById("welcome-message").textContent = "¡Hola " + username + "!";
console.log("El usuario se llama " + username);

if(!username){
    alert("Nombre no ingresado.");
}else{
    console.log("Nombre ingresado correctamente.");
}

