const QUESTIONS = [
  {
    q: "¿Cuál es más rápido de desarrollar gracias a su alto nivel de abstracción?",
    answer: "python",
    explain: "Django incluye de fábrica ORM, panel de administración, sistema de autenticación y enrutamiento, por lo que resuelves un CRUD completo en pocas líneas. FastAPI suma a eso validación automática de datos y documentación interactiva generada sola. La otra cara: ese mismo 'todo incluido' de Django puede sentirse pesado en proyectos pequeños, y FastAPI, al ser más minimalista, deja más decisiones de arquitectura en tus manos."
  },
  {
    q: "¿Cuál corre igual de bien en Linux y Windows?",
    answer: "dotnet",
    explain: ".NET dejó de depender exclusivamente de Windows desde que se rediseñó como .NET Core: hoy corre igual en Linux, macOS y Windows, lo que facilita desplegarlo en servidores Linux (los más comunes en la nube) sin perder el ecosistema de C#. Antes del cambio, el .NET Framework clásico sí estaba atado a Windows; esa es la razón por la que esta ventaja se destaca como algo relativamente reciente."
  },
  {
    q: "¿Cuál tiene el despliegue y hosting más sencillo por su ecosistema?",
    answer: "php",
    explain: "PHP es compatible con prácticamente cualquier proveedor de hosting compartido y económico, porque el propio lenguaje nació pensado para ejecutarse en servidores web sin configuración especial. Laravel añade herramientas como Forge o Vapor que automatizan aún más el despliegue. La contraparte es que ese hosting barato suele venir con menos control fino sobre el rendimiento que tendrías en un servidor configurado a medida para otro framework."
  },
  {
    q: "¿Cuál es más robusto y maduro para sistemas empresariales grandes?",
    answer: "java",
    explain: "Java lleva más de 25 años usándose en bancos, aseguradoras y grandes corporativos, así que Spring Boot hereda un ecosistema enorme de librerías probadas en producción, herramientas de monitoreo y patrones de diseño ya estandarizados para sistemas complejos. Esa misma madurez trae más capas y configuración que dominar antes de sentirte productivo, comparado con frameworks más ligeros."
  },
  {
    q: "¿Cuál tiene mayor curva de aprendizaje inicial?",
    answer: "java",
    explain: "Spring Boot exige entender conceptos como inyección de dependencias, anotaciones, contenedores de beans y una estructura de proyecto más rígida antes de escribir tu primer endpoint funcional. A cambio de esa inversión inicial, obtienes un framework diseñado para escalar en equipos grandes y proyectos de larga duración, donde esa estructura termina ordenando el trabajo en vez de estorbar."
  },
  {
    q: "¿Cuál es ideal si el proyecto usa mucha ciencia de datos?",
    answer: "python",
    explain: "Python conecta directamente con librerías como pandas, NumPy, scikit-learn o PyTorch, lo que permite que el mismo lenguaje del backend se use para entrenar modelos o procesar datos, sin cambiar de tecnología. Esto es una ventaja enorme si tu equipo ya trabaja en ciencia de datos, aunque para APIs puramente transaccionales sin componente analítico, esta fortaleza deja de pesar tanto en la decisión."
  },
  {
    q: "¿Cuál es fuertemente tipado y de excelente rendimiento?",
    answer: "dotnet",
    explain: "C# obliga a declarar tipos de datos explícitos, lo que atrapa muchos errores antes de ejecutar el programa (en tiempo de compilación) en vez de que aparezcan ya en producción. Sumado a esto, el runtime de .NET está muy optimizado, entregando tiempos de respuesta comparables a los de lenguajes compilados como Java o Go. El costo es que ese tipado estricto pide más código explícito que un lenguaje dinámico como Python."
  },
  {
    q: "¿Cuál rinde menos por ser interpretado y no compilado?",
    answer: "php",
    explain: "PHP tradicionalmente se ejecuta interpretando el código en cada petición, lo que consume más recursos que un lenguaje compilado directamente a código máquina o a bytecode optimizado como Java o .NET. Versiones recientes de PHP y herramientas como OPcache han reducido bastante esta brecha, pero en cargas de trabajo muy pesadas o en tiempo real, sigue siendo la opción menos eficiente de las cuatro."
  }
];

const FW = {
  java:   { name: "Java / Spring",     cls: "java" },
  python: { name: "Python / Django",   cls: "python" },
  dotnet: { name: ".NET Core",         cls: "dotnet" },
  php:    { name: "PHP / Laravel",     cls: "php" }
};

let order = QUESTIONS.map((_, idx) => idx);
let i = 0;
let score = 0;
let answered = false;

function shuffle(arr) {
  for (let k = arr.length - 1; k > 0; k--) {
    const j = Math.floor(Math.random() * (k + 1));
    [arr[k], arr[j]] = [arr[j], arr[k]];
  }
  return arr;
}

function render() {
  const app = document.getElementById('app');

  if (i >= order.length) {
    app.innerHTML = `
      <div class="result">
        <div class="score">${score} / ${QUESTIONS.length}</div>
        <div class="msg">Vuelve a jugar para reforzar lo aprendido.</div>
        <button class="restart-btn" id="restart">Jugar de nuevo</button>
      </div>
    `;
    document.getElementById('restart').onclick = () => {
      order = shuffle(QUESTIONS.map((_, idx) => idx));
      i = 0;
      score = 0;
      render();
    };
    return;
  }

  const item = QUESTIONS[order[i]];
  app.innerHTML = `
    <div class="top-row">
      <span>Pregunta ${i + 1} de ${QUESTIONS.length}</span>
      <span>Puntaje: ${score}</span>
    </div>
    <div class="question">${item.q}</div>
    <div class="options" id="options">
      ${Object.entries(FW).map(([key, f]) => `
        <button class="opt-btn" data-key="${key}">
          <span class="dot ${f.cls}"></span>${f.name}
        </button>
      `).join('')}
    </div>
    <div id="feedback"></div>
  `;

  answered = false;

  document.querySelectorAll('#options .opt-btn').forEach(btn => {
    btn.onclick = () => {
      if (answered) return;
      answered = true;

      const key = btn.getAttribute('data-key');
      const correct = key === item.answer;
      if (correct) score++;

      document.querySelectorAll('#options .opt-btn').forEach(b => {
        const bk = b.getAttribute('data-key');
        if (bk === item.answer) b.classList.add('correct');
        else if (bk === key) b.classList.add('wrong');
        b.disabled = true;
      });

      const isLast = i + 1 >= order.length;
      document.getElementById('feedback').innerHTML = `
        <div class="feedback">
          <div class="verdict ${correct ? 'ok' : 'bad'}">${correct ? 'Correcto.' : 'No exactamente.'}</div>
          <div class="explain">${item.explain}</div>
          <button class="next-btn" id="next">${isLast ? 'Ver resultado' : 'Siguiente'}</button>
        </div>
      `;
      document.getElementById('next').onclick = () => { i++; render(); };
    };
  });
}

order = shuffle(order);
render();