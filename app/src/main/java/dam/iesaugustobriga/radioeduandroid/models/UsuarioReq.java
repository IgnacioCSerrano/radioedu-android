package dam.iesaugustobriga.radioeduandroid.models;

@SuppressWarnings("unused")
public class UsuarioReq {

    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String nombre;
    private String apellidos;
    private int codigoCentro;

    public UsuarioReq() {
        this.username = null;
        this.password = null;
        this.confirmPassword = null;
        this.email = null;
        this.nombre = null;
        this.apellidos = null;
        this.codigoCentro = 0;
    }

    public UsuarioReq(String username, String password, String confirmPassword, String email, String nombre, String apellidos, int codigoCentro) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.codigoCentro = codigoCentro;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

}
