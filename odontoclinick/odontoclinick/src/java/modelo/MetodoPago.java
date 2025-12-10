package modelo;

public class MetodoPago {
    private int id_metodo_pago;
    private String nombre_metodo;
    private boolean activo;
    
    public int getId_metodo_pago() {
        return id_metodo_pago;
    }
    public void setId_metodo_pago(int id_metodo_pago) {
        this.id_metodo_pago = id_metodo_pago;
    }
    public String getNombre_metodo() {
        return nombre_metodo;
    }
    public void setNombre_metodo(String nombre_metodo) {
        this.nombre_metodo = nombre_metodo;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
