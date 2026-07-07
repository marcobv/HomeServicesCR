// HomeServices CR - JavaScript mínimo para confirmaciones básicas.
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('[data-confirm]').forEach((element) => {
        element.addEventListener('click', (event) => {
            const message = element.getAttribute('data-confirm') || '¿Desea continuar?';
            if (!confirm(message)) {
                event.preventDefault();
            }
        });
    });
});
