const messages = [
    {
        sender: "Bootstrap 🅱️",
        classSender: "bootstrap",
        text: "A ver, bajen un cambio que llegaron los pesos pesados. La v5.3 los pasa por encima. Ya enterré a jQuery, soy puro Vanilla JS, vuelo. Y para los que lloran por la personalización, mi nueva Utility API y mi soporte nativo para Dark Mode los deja sin excusas.",
        time: "10:15 AM",
        isMe: false
    },
    {
        sender: "Tailwind 💨",
        classSender: "tailwind",
        text: "Puro humo. Mucha API, pero sigues pesando una tonelada y todas tus webs parecen clones del 2014. Yo no te doy componentes armados para vagos; te doy clases utilitarias de bajo nivel en el HTML. Control total y cero código basura. Ustedes son plantillas glorificadas.",
        time: "10:18 AM",
        isMe: false
    },
    {
        sender: "Materialize Ⓜ️",
        classSender: "materialize",
        text: "Sigan peleando por quién escribe el código más feo. Yo sigo las reglas estrictas de Material Design de Google. Efectos Ripple, Parallax nativo y animaciones fluidas directo de la caja. Hago interfaces de alto nivel sin que tengan que ajustar pixel por pixel como novatos.",
        time: "10:21 AM",
        isMe: false
    },
    {
        sender: "Bootstrap 🅱️",
        classSender: "bootstrap",
        text: "¿Alto nivel tú? Llevas años estancado. Mis componentes Offcanvas, mi sistema de Dropdowns y mi nueva integración opcional con CSS Grid son el estándar de la industria. Te levanto un layout complejo y responsivo en 3 minutos mientras tú sigues buscando cómo centrar un div.",
        time: "10:24 AM",
        isMe: false
    },
    {
        sender: "Dev (Tú)",
        text: "¡Ya córtenla, pesados! Parecen juniors peleando en un foro. 🛑",
        time: "10:26 AM",
        isMe: true
    },
    {
        sender: "Dev (Tú)",
        text: "Tailwind, sirves cuando hay tiempo de diseñar a medida. Materialize, cuando quiero que algo se vea como app rápido.",
        time: "10:26 AM",
        isMe: true
    },
    {
        sender: "Dev (Tú)",
        text: "Pero Bootstrap, pásame el código de tu 'Toast' de una buena vez.",
        time: "10:27 AM",
        isMe: true
    },
    {
        sender: "Dev (Tú)",
        text: "Tengo al profe respirándome en la nuca, apenas voy a crear el archivo en AlmaLinux a través de MobaXterm y me falta terminar el front. Necesito levantar esa notificación de éxito rápido para salvar la nota. ¡Muévanse!",
        time: "10:28 AM",
        isMe: true
    }
];

document.addEventListener("DOMContentLoaded", () => {
    const chatBox = document.getElementById("chat-box");

    messages.forEach(msg => {
        // Determinar si el mensaje es tuyo o de otro
        const messageTypeClass = msg.isMe ? "me" : "other";
        
        // Determinar el color del remitente (si aplica)
        const senderColorClass = msg.classSender ? msg.classSender : "";
        
        // Solo mostrar el nombre si no es tu propio mensaje
        const senderHTML = !msg.isMe 
            ? `<span class="sender ${senderColorClass}">${msg.sender}</span>` 
            : "";

        const messageHTML = `
            <div class="message ${messageTypeClass}">
                ${senderHTML}
                ${msg.text}
                <span class="time">${msg.time}</span>
            </div>
        `;

        chatBox.innerHTML += messageHTML;
    });

    // Auto-scroll hacia abajo
    chatBox.scrollTop = chatBox.scrollHeight;
});