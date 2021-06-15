package dam.iesaugustobriga.radioeduandroid.models;

import java.io.Serializable;
import java.time.LocalDateTime;

@SuppressWarnings("unused")
public class Podcast implements Serializable {

    private long id;
    private String imagen;
    private String audio;
    private String titulo;
    private String cuerpo;
    private LocalDateTime fechaCreacion;
    private int visitas;
    private int reproducciones;
    private boolean bloqueado;
    private boolean favorito;
    private long idRadio;

    public Podcast(long id, String imagen, String audio, String titulo, String cuerpo, LocalDateTime fechaCreacion, int visitas, int reproducciones, boolean bloqueado, boolean favorito, long idRadio) {
        this.id = id;
        this.imagen = imagen;
        this.audio = audio;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.fechaCreacion = fechaCreacion;
        this.visitas = visitas;
        this.reproducciones = reproducciones;
        this.bloqueado = bloqueado;
        this.favorito = favorito;
        this.idRadio = idRadio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getVisitas() {
        return visitas;
    }

    public void setVisitas(int visitas) {
        this.visitas = visitas;
    }

    public int getReproducciones() {
        return reproducciones;
    }

    public void setReproducciones(int reproducciones) {
        this.reproducciones = reproducciones;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public long getIdRadio() {
        return idRadio;
    }

    public void setIdRadio(long idRadio) {
        this.idRadio = idRadio;
    }

}
