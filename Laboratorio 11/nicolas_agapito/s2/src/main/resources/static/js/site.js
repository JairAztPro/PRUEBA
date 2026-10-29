document.querySelectorAll("[data-confirm-delete]").forEach((form) => {
    form.addEventListener("submit", (event) => {
        const title = form.dataset.courseTitle ?? "este curso";
        if (!window.confirm(`¿Desea eliminar el curso "${title}"?`)) {
            event.preventDefault();
        }
    });
});
