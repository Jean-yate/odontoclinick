package modelo;

import java.util.Date;

public class HistorialMedico {
    private int id_historial, id_paciente, id_cita, id_doctor;
    private String diagnostico, tratamiento_realizado, observaciones;
    private Date fecha, fecha_creacion;
    private Paciente paciente;
    private Cita cita;
    private Medico medico;
    
    public int getId_historial() {
        return id_historial;
    }
    public void setId_historial(int id_historial) {
        this.id_historial = id_historial;
    }
    public int getId_paciente() {
        return id_paciente;
    }
    public void setId_paciente(int id_paciente) {
        this.id_paciente = id_paciente;
    }
    public int getId_cita() {
        return id_cita;
    }
    public void setId_cita(int id_cita) {
        this.id_cita = id_cita;
    }
    public int getId_doctor() {
        return id_doctor;
    }
    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }
    public String getDiagnostico() {
        return diagnostico;
    }
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
    public String getTratamiento_realizado() {
        return tratamiento_realizado;
    }
    public void setTratamiento_realizado(String tratamiento_realizado) {
        this.tratamiento_realizado = tratamiento_realizado;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public Date getFecha_creacion() {
        return fecha_creacion;
    }
    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Cita getCita() {
        return cita;
    }
    public void setCita(Cita cita) {
        this.cita = cita;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
