package pe.edu.utp.techlab.web;

/** Estado minimo de seguridad que necesitan las vistas: hay sesion valida y que nombre mostrar. */
public record SecurityView(
        boolean authenticated,
        String username
) {
    public static SecurityView anonymous() {
        return new SecurityView(false, "");
    }
}
