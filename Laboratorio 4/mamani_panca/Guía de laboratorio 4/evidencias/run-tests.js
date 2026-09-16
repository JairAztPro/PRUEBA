const { chromium } = require('playwright');
const path = require('path');
const fs = require('fs');

const URL = 'file://' + path.resolve(__dirname, '..', 'index.html');
const OUT = __dirname;

const results = [];
function log(caso, ok, detalle) {
  results.push({ caso, ok, detalle });
  console.log(`[${ok ? 'PASA' : 'FALLA'}] ${caso} — ${detalle}`);
}

(async () => {
  const browser = await chromium.launch();

  // ---------- CP-01: consola y red sin errores ----------
  {
    const page = await browser.newPage();
    const consoleErrors = [];
    const failedRequests = [];
    page.on('console', (msg) => { if (msg.type() === 'error') consoleErrors.push(msg.text()); });
    page.on('pageerror', (err) => consoleErrors.push(err.message));
    page.on('requestfailed', (req) => failedRequests.push(req.url()));
    await page.setViewportSize({ width: 1280, height: 900 });
    await page.goto(URL, { waitUntil: 'networkidle' });
    await page.waitForTimeout(500);
    log('CP-01 Consola y red', consoleErrors.length === 0 && failedRequests.length === 0,
      `errores consola=${consoleErrors.length} (${consoleErrors.join(' | ')}); solicitudes fallidas=${failedRequests.length} (${failedRequests.join(' | ')})`);
    await page.close();
  }

  // ---------- Matriz por ancho + capturas ----------
  const anchos = [
    { nombre: '360x800', width: 360, height: 800 },
    { nombre: '768x1024', width: 768, height: 1024 },
    { nombre: '1024x768', width: 1024, height: 768 },
    { nombre: '1440x900', width: 1440, height: 900 },
  ];

  for (const a of anchos) {
    const page = await browser.newPage({ viewport: { width: a.width, height: a.height } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const scrollWidth = await page.evaluate(() => document.documentElement.scrollWidth);
    const clientWidth = await page.evaluate(() => document.documentElement.clientWidth);
    const sinDesborde = scrollWidth <= clientWidth + 1; // tolerancia de 1px
    await page.screenshot({ path: path.join(OUT, `captura-${a.nombre}.png`), fullPage: true });
    log(`Ancho ${a.nombre}`, sinDesborde, `scrollWidth=${scrollWidth} clientWidth=${clientWidth}`);
    await page.close();
  }

  // ---------- Zoom 200% en 1280px ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    // Emular zoom reduciendo el viewport lógico manteniendo el layout (deviceScaleFactor no simula zoom de layout;
    // se aproxima reduciendo el viewport a la mitad del ancho, que es equivalente a 200% de zoom de página).
    await page.setViewportSize({ width: 640, height: 450 });
    await page.waitForTimeout(200);
    const scrollWidth = await page.evaluate(() => document.documentElement.scrollWidth);
    const clientWidth = await page.evaluate(() => document.documentElement.clientWidth);
    await page.screenshot({ path: path.join(OUT, 'captura-zoom200.png'), fullPage: true });
    log('Zoom 200% (aprox.)', scrollWidth <= clientWidth + 1, `scrollWidth=${scrollWidth} clientWidth=${clientWidth}`);
    await page.close();
  }

  // ---------- CP-02: navbar con teclado ----------
  {
    const page = await browser.newPage({ viewport: { width: 375, height: 700 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const toggler = page.locator('.navbar-toggler');
    await toggler.focus();
    const before = await toggler.getAttribute('aria-expanded');
    await page.keyboard.press('Enter');
    await page.waitForTimeout(400);
    const after = await toggler.getAttribute('aria-expanded');
    log('CP-02 Navbar con teclado', before === 'false' && after === 'true',
      `aria-expanded antes=${before} después=${after}`);
    await page.close();
  }

  // ---------- CP-03: enlaces internos ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const destinos = ['#inicio', '#rutas', '#horarios', '#contacto'];
    let todosExisten = true;
    for (const d of destinos) {
      const existe = await page.locator(d).count();
      if (existe === 0) todosExisten = false;
    }
    log('CP-03 Enlaces internos', todosExisten, `secciones verificadas: ${destinos.join(', ')}`);
    await page.close();
  }

  // ---------- CP-04: modal recibe data-curso correcto ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const boton = page.locator('button[data-curso="Backend con Spring Boot"]');
    await boton.click();
    await page.waitForSelector('#modalRegistro.show');
    const valor = await page.locator('#curso').inputValue();
    log('CP-04 Transferencia de curso', valor === 'Backend con Spring Boot', `valor recibido="${valor}"`);
    await page.close();
  }

  // ---------- CP-05: cerrar con Escape y foco vuelve al activador ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const boton = page.locator('button[data-curso="Frontend con Bootstrap"]').first();
    await boton.click();
    await page.waitForSelector('#modalRegistro.show');
    await page.keyboard.press('Escape');
    await page.waitForTimeout(400);
    const modalVisible = await page.locator('#modalRegistro.show').count();
    const focoActivo = await page.evaluate(() => document.activeElement?.dataset?.curso);
    log('CP-05 Escape y retorno de foco', modalVisible === 0 && focoActivo === 'Frontend con Bootstrap',
      `modalVisible=${modalVisible} focoActivo=${focoActivo}`);
    await page.close();
  }

  // ---------- CP-06: envío vacío ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    await page.locator('button[data-curso="Frontend con Bootstrap"]').first().click();
    await page.waitForSelector('#modalRegistro.show');
    await page.locator('button[type="submit"]').click();
    await page.waitForTimeout(200);
    const wasValidated = await page.locator('#formRegistro.was-validated').count();
    const focoInvalido = await page.evaluate(() => document.activeElement?.id);
    const estadoOculto = await page.locator('#estadoFormulario.d-none').count();
    log('CP-06 Envío vacío', wasValidated === 1 && focoInvalido === 'nombre' && estadoOculto === 1,
      `was-validated=${wasValidated} foco=${focoInvalido} estadoOculto=${estadoOculto}`);
    await page.close();
  }

  // ---------- CP-07: correo inválido ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    await page.locator('button[data-curso="Frontend con Bootstrap"]').first().click();
    await page.waitForSelector('#modalRegistro.show');
    await page.fill('#nombre', 'Ana Torres');
    await page.fill('#correo', 'correo-no-valido');
    await page.selectOption('#modalidad', 'virtual');
    await page.check('#acepta');
    await page.locator('button[type="submit"]').click();
    await page.waitForTimeout(200);
    const correoInvalido = await page.evaluate(() => !document.querySelector('#correo').checkValidity());
    const focoCorreo = await page.evaluate(() => document.activeElement?.id === 'correo');
    log('CP-07 Correo inválido', correoInvalido && focoCorreo, `correoInvalido=${correoInvalido} focoEnCorreo=${focoCorreo}`);
    await page.close();
  }

  // ---------- CP-08: datos válidos ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    await page.locator('button[data-curso="Fullstack Web"]').click();
    await page.waitForSelector('#modalRegistro.show');
    await page.fill('#nombre', 'Ana Torres');
    await page.fill('#correo', 'ana.torres@example.com');
    await page.selectOption('#modalidad', 'hibrida');
    await page.check('#acepta');
    await page.locator('button[type="submit"]').click();
    await page.waitForTimeout(200);
    const mensaje = await page.locator('#estadoFormulario').textContent();
    const visible = await page.locator('#estadoFormulario:not(.d-none)').count();
    log('CP-08 Datos válidos', visible === 1 && mensaje.length > 0, `mensaje="${mensaje.trim()}"`);
    await page.close();
  }

  // ---------- CP-09: reset al reabrir ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    await page.locator('button[data-curso="Fullstack Web"]').click();
    await page.waitForSelector('#modalRegistro.show');
    await page.fill('#nombre', 'Ana Torres');
    await page.fill('#correo', 'ana.torres@example.com');
    await page.selectOption('#modalidad', 'hibrida');
    await page.check('#acepta');
    await page.locator('button[type="submit"]').click();
    await page.waitForTimeout(200);
    // cerrar con el botón Cancelar
    await page.locator('#modalRegistro button:has-text("Cancelar")').click();
    await page.waitForTimeout(400);
    // reabrir con otra ruta
    await page.locator('button[data-curso="Frontend con Bootstrap"]').first().click();
    await page.waitForSelector('#modalRegistro.show');
    const nombreVacio = await page.locator('#nombre').inputValue();
    const wasValidated = await page.locator('#formRegistro.was-validated').count();
    const estadoOculto = await page.locator('#estadoFormulario.d-none').count();
    log('CP-09 Reinicio al reabrir', nombreVacio === '' && wasValidated === 0 && estadoOculto === 1,
      `nombre="${nombreVacio}" was-validated=${wasValidated} estadoOculto=${estadoOculto}`);
    await page.close();
  }

  // ---------- CP-10: recorrido completo con teclado (Tab) ----------
  {
    const page = await browser.newPage({ viewport: { width: 1280, height: 900 } });
    await page.goto(URL, { waitUntil: 'networkidle' });
    const focosVistos = [];
    for (let i = 0; i < 15; i++) {
      await page.keyboard.press('Tab');
      const info = await page.evaluate(() => {
        const el = document.activeElement;
        return el ? (el.id || el.className || el.tagName) : null;
      });
      focosVistos.push(info);
    }
    const sinNulos = focosVistos.every((f) => f !== null && f !== 'BODY');
    log('CP-10 Recorrido con Tab', sinNulos, `secuencia de foco: ${focosVistos.join(' -> ')}`);
    await page.close();
  }

  await browser.close();

  // ---------- Guardar resultados ----------
  fs.writeFileSync(path.join(OUT, 'resultados.json'), JSON.stringify(results, null, 2));
  const totalOk = results.filter(r => r.ok).length;
  console.log(`\n${totalOk}/${results.length} casos superados.`);
})();
