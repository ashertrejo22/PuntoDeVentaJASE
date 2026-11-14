package org.uacm.puntodeventa.Clases;

public class Usuario {

    private String usuario;
    private String contra;
    private int nivel;

    // CONTRASEÑAS VARIABLES
    public static String contraAdmin = "Admin96";
    public static String contraEmpleado = "Emple23";

    public Usuario(String usua, String contr) {

        this.usuario = usua;
        this.contra = contr;
        this.nivel = validar(usua, contr);
    }

    public int validar(String us, String con) {

        // ADMINISTRADOR
        if (us.equals("Administrador") &&
            con.equals(contraAdmin)) {

            return 1;
        }

        // EMPLEADO
        if (us.equals("Empleado") &&
            con.equals(contraEmpleado)) {

            return 2;
        }

        return 0;
    }

    // CAMBIAR CONTRASEÑA
    public static void cambiarContra(String usuario,
                                     String nuevaContra) {

        if (usuario.equalsIgnoreCase("Administrador")) {

            contraAdmin = nuevaContra;

        } else if (usuario.equalsIgnoreCase("Empleado")) {

            contraEmpleado = nuevaContra;
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContra() {
        return contra;
    }

    public void setContra(String contra) {
        this.contra = contra;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
}