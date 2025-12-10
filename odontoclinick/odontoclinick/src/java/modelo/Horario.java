package modelo;

import java.sql.Time;

public class Horario {
    private int id_horario, id_doctor, duracion_cita_minutos;
    private String dia_semana;
    private Time hora_inicio, hora_fin;
    private boolean activo;
    private Medico medico;
    
    public int getId_horario() {
        return id_horario;
    }
    public void setId_horario(int id_horario) {
        this.id_horario = id_horario;
    }
    public int getId_doctor() {
        return id_doctor;
    }
    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }
    public int getDuracion_cita_minutos() {
        return duracion_cita_minutos;
    }
    public void setDuracion_cita_minutos(int duracion_cita_minutos) {
        this.duracion_cita_minutos = duracion_cita_minutos;
    }
    public String getDia_semana() {
        return dia_semana;
    }
    public void setDia_semana(String dia_semana) {
        this.dia_semana = dia_semana;
    }
    public Time getHora_inicio() {
        return hora_inicio;
    }
    public void setHora_inicio(Time hora_inicio) {
        this.hora_inicio = hora_inicio;
    }
    public Time getHora_fin() {
        return hora_fin;
    }
    public void setHora_fin(Time hora_fin) {
        this.hora_fin = hora_fin;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
