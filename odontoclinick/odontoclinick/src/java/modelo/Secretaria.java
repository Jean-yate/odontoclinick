package modelo;

import java.util.Date;

public class Secretaria {
    private int id_secretaria, id_usuario;
    private String turno;
    private Date fecha_ingreso;
    private Usuario usuario;
    
    public int getId_secretaria() {
        return id_secretaria;
    }
    public void setId_secretaria(int id_secretaria) {
        this.id_secretaria = id_secretaria;
    }
    public int getId_usuario() {
        return id_usuario;
    }
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    public String getTurno() {
        return turno;
    }
    public void setTurno(String turno) {
        this.turno = turno;
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
}
