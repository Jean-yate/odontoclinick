package modelo;

public class EstadoCita {
    private int id_estado_cita;
    private String nombre_estado, color;
    
    public int getId_estado_cita() {
        return id_estado_cita;
    }
    public void setId_estado_cita(int id_estado_cita) {
        this.id_estado_cita = id_estado_cita;
    }
    public String getNombre_estado() {
        return nombre_estado;
    }
    public void setNombre_estado(String nombre_estado) {
        this.nombre_estado = nombre_estado;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}