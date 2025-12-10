package modelo;

public class Especialidad {
    private int id_especialidad;
    private String nombre_especialidad, descripcion;
    
    public int getId_especialidad() {
        return id_especialidad;
    }
    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }
    public String getNombre_especialidad() {
        return nombre_especialidad;
    }
    public void setNombre_especialidad(String nombre_especialidad) {
        this.nombre_especialidad = nombre_especialidad;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}