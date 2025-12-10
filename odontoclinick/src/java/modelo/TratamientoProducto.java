package modelo;

import java.math.BigDecimal;

class TratamientoProducto {
    private int idTratamientoProducto, idTratamiento, idProducto;
    private BigDecimal cantidadRequerida;

    public int getIdTratamientoProducto() {
        return idTratamientoProducto;
    }

    public void setIdTratamientoProducto(int idTratamientoProducto) {
        this.idTratamientoProducto = idTratamientoProducto;
    }

    public int getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(int idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public BigDecimal getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(BigDecimal cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }
}
