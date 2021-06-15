package dam.iesaugustobriga.radioeduandroid.models;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Radio implements Serializable {

    private long id;
    private String nombre;
    private String imagen;
    private String nombreCentro;
    private String localidadCentro;
    private boolean suscrito;

    public Radio(long id, String nombre, String imagen, String nombreCentro, String localidadCentro, boolean suscrito) {
        this.id = id;
        this.nombre = nombre;
        this.imagen = imagen;
        this.nombreCentro = nombreCentro;
        this.localidadCentro = localidadCentro;
        this.suscrito = suscrito;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getNombreCentro() {
        return nombreCentro;
    }

    public void setNombreCentro(String nombreCentro) {
        this.nombreCentro = nombreCentro;
    }

    public String getLocalidadCentro() {
        return localidadCentro;
    }

    public void setLocalidadCentro(String localidadCentro) {
        this.localidadCentro = localidadCentro;
    }

    public boolean isSuscrito() {
        return suscrito;
    }

    public void setSuscrito(boolean suscrito) {
        this.suscrito = suscrito;
    }

}
