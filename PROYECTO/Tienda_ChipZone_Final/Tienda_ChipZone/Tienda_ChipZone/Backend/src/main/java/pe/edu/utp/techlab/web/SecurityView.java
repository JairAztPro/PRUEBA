package pe.edu.utp.techlab.web;

/**
 * Estado minimo de seguridad que necesitan las vistas: hay sesion valida, que nombre mostrar y si
 * la persona es administradora (solo controla la visibilidad de botones; la regla real esta en
 * SecurityConfig).
 */
public record SecurityView(
        boolean authenticated,
        String username,
        boolean admin
) {
    public static SecurityView anonymous() {
        return new SecurityView(false, "", false);
    }
}
