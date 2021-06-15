package dam.iesaugustobriga.radioeduandroid.models;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Centro {

    private int codigo;
    private String denominacion;

    public Centro() {
        this.codigo = 0;
        this.denominacion = null;
    }

    public Centro(int codigo, String denominacion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    @NotNull
    @Override
    public String toString() {
        return this.denominacion;
    }

}
