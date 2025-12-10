package modelo;


import java.util.Date;

public class Medico {
    private int id_doctor, id_usuario, id_especialidad, anos_experiencia;
    private String licencia_medica;
    private Date fecha_ingreso;
    private Usuario usuario;
    private Especialidad especialidad;
    
    public int getId_doctor() {
        return id_doctor;
    }
    public void setId_doctor(int id_doctor) {
        this.id_doctor = id_doctor;
    }
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    public int getId_especialidad() {
        return id_especialidad;
    }
    public void setId_especialidad(int id_especialidad) {
        this.id_especialidad = id_especialidad;
    }
    public int getAnos_experiencia() {
        return anos_experiencia;
    }
    public void setAnos_experiencia(int anos_experiencia) {
        this.anos_experiencia = anos_experiencia;
    }
    public String getLicencia_medica() {
        return licencia_medica;
    }
    public void setLicencia_medica(String licencia_medica) {
        this.licencia_medica = licencia_medica;
    }
    public Date getFecha_ingreso() {
        return fecha_ingreso;
    }
    public void setFecha_ingreso(Date fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Especialidad getEspecialidad() {
        return especialidad;
    }
    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}
