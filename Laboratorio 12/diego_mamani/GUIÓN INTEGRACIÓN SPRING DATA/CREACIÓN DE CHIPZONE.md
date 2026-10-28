### Guion teatral: «Operación ChipZone: ¡la tienda tiene que funcionar!»

Personaje 1: Alex

Diseñador front-end. Creativo, obsesionado con que la tienda se vea bien.

Personaje 2: Sam

Desarrollador back-end. Resuelve la lógica y conecta las páginas.

Personaje 3: Dani

Especialista en datos. Se asegura de que la información sea válida y se almacene.

Duración estimada: 12–15 minutos. Las acotaciones entre corchetes son acciones teatrales, no texto para leer.

### Escena 1. ¡Tenemos un problema!

Alex entra mirando su laptop con entusiasmo. Sam está revisando código y Dani observa una tabla de productos.

ALEX — FRONT-END

“¡Equipo, buenas noticias! Ya tengo el diseño de ChipZone. Miren este catálogo: teclados mecánicos, mouse gaming, audífonos, monitores… ¡parece una tienda profesional!”

\[Alex gira la laptop para mostrar la página.\]

SAM — BACK-END

“Muy bonito, Alex. Una pregunta: ¿puedes registrar un producto nuevo?”

ALEX

“Claro. Puedo ponerle un botón que diga ‘Agregar producto’”.

SAM

“No te pregunté cómo se llama el botón. Te pregunté si realmente guarda el producto”.

\[Alex mira la pantalla y se queda en silencio unos segundos.\]

ALEX

“Bueno… el botón está bonito”.

DANI — BASE DE DATOS

“¡Fantástico! Tenemos una tienda donde puedes ver los productos, pero no necesariamente registrarlos. Y aunque logremos registrarlos, ¿dónde vamos a guardar los datos? ¿En la memoria de Alex?”

\[Sam y Dani miran a Alex. Pausa cómica.\]

ALEX

“Está bien, está bien. Necesitamos algo más que una interfaz. Vamos a construir ChipZone como corresponde”.

\[Los tres se colocan frente a la pantalla. Aparece el título: «Operación ChipZone».\]

### Escena 2. Primera misión: construir una interfaz funcional

Unidad 1 · Semanas 1–4

ALEX

“Primero, debemos entender el front-end. HTML organiza el contenido, CSS se encarga de su apariencia y JavaScript permite añadir interactividad”.

SAM

“¿Y vas a escribir todo el diseño desde cero? ¿Cada botón, cada formulario, cada columna?”

ALEX

“¡No necesariamente! Para eso tenemos Bootstrap. Es un framework de front-end que nos proporciona clases y componentes reutilizables”.

\[Alex muestra el catálogo.\]

“Con sus contenedores, filas y columnas podemos organizar los productos. También podemos crear una barra de navegación, utilizar imágenes responsivas, incorporar íconos y diseñar formularios”.

DANI

“Espera. Si alguien abre ChipZone desde un celular, ¿los productos van a salir amontonados como cajas en un almacén?”

ALEX

“¡No! Para eso está el diseño responsivo. Las columnas de Bootstrap pueden adaptarse al tamaño de la pantalla”.

SAM

“¿Y las ventanas que aparecen sobre la página, por ejemplo, para mostrar detalles de un producto?”

ALEX

“Podemos utilizar modales de Bootstrap. También podemos dar estilo a formularios, botones y otros componentes”.

DANI

“Entonces ya tenemos una tienda visualmente atractiva. Pero sigo sin ver dónde se guardan los productos”.

ALEX

“¡Otra vez con lo mismo! Está bien, tu turno. Necesitamos un servidor”.

Interacción con el público: Alex puede preguntar: “¿Qué framework estamos utilizando para organizar la interfaz?” El público responde: “¡Bootstrap!”.

Acción para la exposición

Mostrar la página real de ChipZone. Alex señala los componentes de Bootstrap mientras Sam y Dani hacen las preguntas. Si tienen una vista móvil disponible, pueden alternar entre ambas.

### Escena 3. Segunda misión: el servidor debe responder

Unidad 2 · Semanas 5–8

\[Sam se levanta y toma el control de la presentación.\]

SAM

“Ahora sí. La interfaz necesita comunicarse con el back-end. Para eso utilizamos Java y Spring Boot, que facilita la configuración y ejecución de la aplicación”.

ALEX

“Traducido al idioma de los diseñadores: ¿qué hace exactamente Spring Boot?”

SAM

“Nos ayuda a preparar el entorno para que la aplicación pueda ejecutarse. Dentro de ese proyecto utilizamos Spring Web para atender solicitudes HTTP mediante controladores y rutas”.

DANI

“Haz una demostración. Quiero consultar los productos”.

SAM

“Imaginemos que accedes a la ruta `/productos`. Un controlador puede atender esa solicitud y preparar los datos que necesita la página”.

ALEX

“¿Y cómo haces para que esos datos aparezcan dentro de mi HTML sin escribir cada producto manualmente?”

SAM

“¡Con Thymeleaf! Es un motor de plantillas que permite incorporar datos enviados desde Java dentro del HTML”.

DANI

“Pero no me digas que vas a escribir los precios directamente en el código. ¿Qué pasa si cambia el precio del teclado?”

SAM

“Precisamente por eso necesitamos separar los datos de la plantilla. Thymeleaf presenta los valores que recibe; la forma de obtenerlos depende de la lógica del servidor y de la fuente de datos”.

ALEX

“Entonces tú preparas la información y Thymeleaf me ayuda a mostrarla. Ahora sí estamos hablando el mismo idioma”.

Acción para la exposición

Sam muestra un controlador Java y, después, una plantilla Thymeleaf. Alex señala dónde se verá el resultado y Dani pregunta de dónde provienen los datos.

### Escena 4. Tercera misión: los datos no pueden desaparecer

Unidad 3 · Semanas 9–12

\[Dani se acerca a la pantalla y muestra una tabla de productos.\]

DANI

“Por fin llegamos a mi especialidad: la base de datos. ChipZone necesita conservar sus productos, así que utilizamos MySQL para almacenar información organizada en tablas”.

ALEX

“Perfecto. Quiero una tabla con el nombre, precio, stock y categoría de cada producto”.

DANI

“Eso podemos organizarlo en una tabla de productos. Pero hay otro desafío: ¿cómo conectamos los objetos de Java con los registros de MySQL?”

SAM

“Mediante JPA, Hibernate y Spring Data JPA. JPA define las reglas de persistencia; Hibernate implementa esa especificación y Spring Data JPA facilita el acceso a los datos con repositorios”.

ALEX

“Muy bien. Quiero registrar un teclado mecánico de S/ 250. ¿Qué hacemos?”

DANI

“Primero, recibimos los datos. Luego debemos comprobar que cumplan las reglas establecidas. No queremos guardar un producto sin nombre o con un precio negativo”.

SAM

“Para eso podemos utilizar validaciones en la aplicación, por ejemplo mediante Jakarta Bean Validation integrada con Spring, y también restricciones de integridad en MySQL”.

ALEX

“¿Y si después queremos cambiar el precio?”

DANI

“Actualizamos el registro”.

ALEX

“¿Y si quiero ver todos los teclados?”

DANI

“Consultamos los productos”.

SAM

“Y esas operaciones forman parte del CRUD: crear, consultar, actualizar y eliminar. ¡Las cuatro operaciones fundamentales para administrar nuestros registros!”

DANI

“Exactamente. Así dejamos de tener solamente una página bonita y empezamos a construir una aplicación capaz de gestionar información”.

Mini reto interactivo para los tres

¿Qué operación CRUD corresponde a cada situación?

Situación: el administrador cambia el precio de un mouse de S/ 120 a S/ 100.

Crear

Consultar

Actualizar

Eliminar

Comprobar respuesta

### Escena 5. La prueba final: ¿funciona ChipZone?

Los tres se colocan frente a la pantalla. Alex representa al usuario que necesita registrar un producto, Sam procesa la solicitud y Dani comprueba la persistencia.

ALEX

“¡Atención, equipo! Vamos a registrar unos audífonos gaming. El nombre es ‘HyperSound’, el precio es S/ 180 y tenemos 15 unidades en stock”.

\[Alex simula completar un formulario.\]

SAM

“Recibo la solicitud desde el servidor. El controlador procesa los datos y coordina la validación antes de solicitar su almacenamiento”.

DANI

“Compruebo que los datos cumplan las reglas y, si todo está correcto, la capa de persistencia guarda el registro en MySQL”.

\[Sam hace una señal a Alex.\]

SAM

“¡Operación completada! Ahora podemos volver a consultar el catálogo”.

ALEX

“¡Y ahí están nuestros audífonos! El usuario ve el producto gracias a la interfaz y a los datos que proporciona el servidor”.

DANI

“Pero atención: esto solo funciona de verdad si todas esas operaciones están implementadas y conectadas en el proyecto. Una demostración simulada explica el proceso, pero no sustituye la prueba real”.

SAM

“De acuerdo. Lo importante es que cada integrante comprende su parte y cómo se conecta con las demás”.

### El recorrido del producto

Alex · Front-end

Formulario HTML y Bootstrap

Sam · Back-end

Spring Web y controlador Java

Dani · Persistencia

JPA, Hibernate y MySQL

Resultado

El producto puede consultarse en el catálogo.

Esquema conceptual. La respuesta del servidor regresa al navegador.

### Escena 6. Cierre: los tres tienen la última palabra

ALEX

“Aprendimos que una buena aplicación necesita una interfaz clara y adaptable”.

SAM

“Y que el servidor debe procesar solicitudes y coordinar la lógica de la aplicación”.

DANI

“Además, la información debe almacenarse y validarse correctamente para que el sistema sea confiable”.

ALEX

“Yo diseño la experiencia…”

SAM

“Yo conecto la lógica…”

DANI

“Y yo conservo los datos”.

LOS TRES, A LA VEZ

“¡Y juntos hacemos funcionar ChipZone! Muchas gracias”.
