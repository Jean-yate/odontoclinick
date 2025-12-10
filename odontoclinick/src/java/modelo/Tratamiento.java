package modelo;

public class Tratamiento {
    private int id_tratamiento, duracion_estimada_minutos;
    private String codigo, nombre_tratamiento, descripcion;
    private float costo_base;
    private boolean activo;
    
    public int getId_tratamiento() {
        return id_tratamiento;
    }
    public void setId_tratamiento(int id_tratamiento) {
        this.id_tratamiento = id_tratamiento;
    }
    public int getDuracion_estimada_minutos() {
        return duracion_estimada_minutos;
    }
    public void setDuracion_estimada_minutos(int duracion_estimada_minutos) {
        this.duracion_estimada_minutos = duracion_estimada_minutos;
    }
    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getNombre_tratamiento() {
        return nombre_tratamiento;
    }
    public void setNombre_tratamiento(String nombre_tratamiento) {
        this.nombre_tratamiento = nombre_tratamiento;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public float getCosto_base() {
        return costo_base;
    }
    public void setCosto_base(float costo_base) {
        this.costo_base = costo_base;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
