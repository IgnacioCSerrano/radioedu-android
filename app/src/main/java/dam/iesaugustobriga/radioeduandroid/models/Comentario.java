package dam.iesaugustobriga.radioeduandroid.models;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class Comentario implements Serializable {

    private long id;
    private String mensaje;
    private LocalDateTime fechaRegistro;
    private long idPodcast;
    private long idUsuario;
    private String nombreUsuario;
    private String imagen;
    private String rol;

    public Comentario(long id, String mensaje, LocalDateTime fechaRegistro, long idPodcast, long idUsuario, String nombreUsuario, String imagen, String rol) {
        this.id = id;
        this.mensaje = mensaje;
        this.fechaRegistro = fechaRegistro;
        this.idPodcast = idPodcast;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.imagen = imagen;
        this.rol = rol;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public long getIdPodcast() {
        return idPodcast;
    }

    public void setIdPodcast(long idPodcast) {
        this.idPodcast = idPodcast;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

}
