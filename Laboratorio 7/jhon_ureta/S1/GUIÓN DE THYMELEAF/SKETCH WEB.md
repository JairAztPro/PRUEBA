👥 Personajes
Thymeleaf → protagonista.
HTML → la página web.
Java / Spring Boot → procesa y entrega los datos.
Base de datos → guarda la información.
Alumno + narrador → consulta la información y explica lo que sucede.
🎭 Sketch: “Thymeleaf en la escuela”

Escena 1 — El problema
Alumno:
—Quiero entrar al sistema de mi colegio para ver mi nota de Programación.
HTML:
—Yo tengo la página, pero estoy vacío. ¡No tengo la información del alumno!
Narrador:
—HTML tiene la estructura de la página, pero necesita recibir información dinámica.

Escena 2 — Aparece Thymeleaf
Thymeleaf:
—¡Yo puedo ayudarte!
Alumno:
—¿Quién eres?
Thymeleaf:
—Soy Thymeleaf, un motor de plantillas. Ayudo a que HTML muestre información dinámica que viene de una aplicación Java.
HTML:
—¿Entonces tú puedes colocar información dentro de mí?
Thymeleaf:
—¡Exactamente!

Escena 3 — Llegan los datos
Base de datos:
—Yo tengo guardada la información:
Alumno: Efraín
Curso: Programación
Nota: 18
Java / Spring Boot:
—Yo proceso esos datos y se los entregaré a Thymeleaf.
Thymeleaf:
—¡Perfecto! Ahora yo los llevaré hacia HTML.

Escena 4 — Thymeleaf hace su trabajo
Thymeleaf:
—HTML, usando atributos como th:text, puedo indicarte qué información mostrar.
HTML
<p th:text="${alumno.nombre}"></p>
<p th:text="${alumno.nota}"></p>
HTML:
—¡Entendido! Ahora puedo mostrar los datos.
HTML:
—Alumno: Efraín
—Curso: Programación
—Nota: 18

Escena 5 — Final
Alumno:
—¡Ahora sí puedo ver mi información!
Narrador:
—Y así Thymeleaf permite que una página HTML deje de ser estática y pueda mostrar información dinámica.
Base de datos:
—Yo guardo los datos.
Java:
—Yo los proceso.
Thymeleaf:
—Yo ayudo a colocarlos en la página.
HTML:
—Yo los muestro.
Alumno:
—¡Y yo puedo verlos!
🎤 Frase final de todos:
“Base de datos guarda → Java procesa → Thymeleaf coloca → HTML muestra → el usuario ve.”