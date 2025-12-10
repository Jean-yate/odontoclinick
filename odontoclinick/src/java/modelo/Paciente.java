package modelo;

import java.util.Date;

public class Paciente {
    private int id_paciente, id_usuario;
    private String direccion, eps, rh, alergias, enfermedades_preexistentes;
    private String contacto_emergencia_nombre, contacto_emergencia_telefono;
    private Date fecha_nacimiento, fecha_registro;
    private Usuario usuario;
    
    public int getId_paciente() {
        return id_paciente;
    }
    public void setId_paciente(int id_paciente) {
        this.id_paciente = id_paciente;
    }
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    public String getDireccion() {
        return direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getEps() {
        return eps;
    }
    public void setEps(String eps) {
        this.eps = eps;
    }
    public String getRh() {
        return rh;
    }
    public void setRh(String rh) {
        this.rh = rh;
    }
    public String getAlergias() {
        return alergias;
    }
    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }
    public String getEnfermedades_preexistentes() {
        return enfermedades_preexistentes;
    }
    public void setEnfermedades_preexistentes(String enfermedades_preexistentes) {
        this.enfermedades_preexistentes = enfermedades_preexistentes;
    }
    public String getContacto_emergencia_nombre() {
        return contacto_emergencia_nombre;
    }
    public void setContacto_emergencia_nombre(String contacto_emergencia_nombre) {
        this.contacto_emergencia_nombre = contacto_emergencia_nombre;
    }
    public String getContacto_emergencia_telefono() {
        return contacto_emergencia_telefono;
    }
    public void setContacto_emergencia_telefono(String contacto_emergencia_telefono) {
        this.contacto_emergencia_telefono = contacto_emergencia_telefono;
    }
    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }
    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
    public Date getFecha_registro() {
        return fecha_registro;
    }
    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
