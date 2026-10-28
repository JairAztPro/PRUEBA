// Confirmacion antes de eliminar un producto (formularios con data-confirm-delete).
document.querySelectorAll("[data-confirm-delete]").forEach((form) => {
    form.addEventListener("submit", (event) => {
        const nombre = form.dataset.productoNombre ?? "este producto";
        if (!window.confirm(`¿Desea eliminar el producto "${nombre}"?`)) {
            event.preventDefault();
        }
    });
});
