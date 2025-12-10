package modelo;

import java.util.Date;

public class Cita {
    private int id_cita, id_paciente, id_doctor, id_estado_cita;
    private String notas_paciente, notas_doctor;
    private Date fecha_hora, fecha_creacion, fecha_actualizacion;
    private Paciente paciente;
    private Medico medico;
    private EstadoCita estadoCita;
    
    public int getId_cita() {
        return id_cita;
    }
    public void setId_cita(int id_cita) {
        this.id_cita = id_cita;
    }
    public int getId_paciente() {
        return id_paciente;
    }
    public void setId_paciente(int id_paciente) {
        this.id_paciente = id_paciente;
    }
    public int getId_doctor() {
        return id_doctor;
    }
    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }
    public int getId_estado_cita() {
        return id_estado_cita;
    }
    public void setId_estado_cita(int id_estado_cita) {
        this.id_estado_cita = id_estado_cita;
    }
    public String getNotas_paciente() {
        return notas_paciente;
    }
    public void setNotas_paciente(String notas_paciente) {
        this.notas_paciente = notas_paciente;
    }
    public String getNotas_doctor() {
        return notas_doctor;
    }
    public void setNotas_doctor(String notas_doctor) {
        this.notas_doctor = notas_doctor;
    }
    public Date getFecha_hora() {
        return fecha_hora;
    }
    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
    public Date getFecha_creacion() {
        return fecha_creacion;
    }
    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    public Date getFecha_actualizacion() {
        return fecha_actualizacion;
    }
    public void setFecha_actualizacion(Date fecha_actualizacion) {
        this.fecha_actualizacion = fecha_actualizacion;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public EstadoCita getEstadoCita() {
        return estadoCita;
    }
    public void setEstadoCita(EstadoCita estadoCita) {
        this.estadoCita = estadoCita;
    }
}