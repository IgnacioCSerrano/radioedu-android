package dam.iesaugustobriga.radioeduandroid.models;

@SuppressWarnings("unused")
public class UsuarioRes {

    private String username;
    private String email;
    private String nombre;
    private String apellidos;
    private int codigoCentro;
    private String provinciaCentro;
    private String localidadCentro;
    private String nombreCentro;

    public UsuarioRes(String username, String email, String nombre, String apellidos, int codigoCentro, String provinciaCentro, String localidadCentro, String nombreCentro) {
        this.username = username;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codigoCentro = codigoCentro;
        this.provinciaCentro = provinciaCentro;
        this.localidadCentro = localidadCentro;
        this.nombreCentro = nombreCentro;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getCodigoCentro() {
        return codigoCentro;
    }

    public void setCodigoCentro(int codigoCentro) {
        this.codigoCentro = codigoCentro;
    }

    public String getProvinciaCentro() {
        return provinciaCentro;
    }

    public void setProvinciaCentro(String provinciaCentro) {
        this.provinciaCentro = provinciaCentro;
    }

    public String getLocalidadCentro() {
        return localidadCentro;
    }

    public void setLocalidadCentro(String localidadCentro) {
        this.localidadCentro = localidadCentro;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

}
