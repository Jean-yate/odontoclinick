package modelo;

public class CitaTratamiento {
    private int id_cita_tratamiento, id_cita, id_tratamiento;
    private String observaciones;
    private float costo_aplicado;
    private boolean completado;
    private Cita cita;
    private Tratamiento tratamiento;
    
    public int getId_cita_tratamiento() {
        return id_cita_tratamiento;
    }
    public void setId_cita_tratamiento(int id_cita_tratamiento) {
        this.id_cita_tratamiento = id_cita_tratamiento;
    }
    public int getId_cita() {
        return id_cita;
    }
    public void setId_cita(int id_cita) {
        this.id_cita = id_cita;
    }
    public int getId_tratamiento() {
        return id_tratamiento;
    }
    public void setId_tratamiento(int id_tratamiento) {
        this.id_tratamiento = id_tratamiento;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public float getCosto_aplicado() {
        return costo_aplicado;
    }
    public void setCosto_aplicado(float costo_aplicado) {
        this.costo_aplicado = costo_aplicado;
    }
    public boolean isCompletado() {
        return completado;
    }
    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
    public Cita getCita() {
        return cita;
    }
    public void setCita(Cita cita) {
        this.cita = cita;
    }
    public Tratamiento getTratamiento() {
        return tratamiento;
    }
    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }
}