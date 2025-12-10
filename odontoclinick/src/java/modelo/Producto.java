package modelo;

import java.util.Date;

public class Producto {
    private int id_producto, id_categoria, stock_actual, stock_minimo;
    private String codigo_producto, nombre_producto, descripcion;
    private float precio_compra, precio_venta;
    private Date fecha_vencimiento, fecha_creacion;
    private boolean activo;
    private CategoriaProducto categoria;
    
    public int getId_producto() {
        return id_producto;
    }
    public void setId_producto(int id_producto) {
        this.id_producto = id_producto;
    }
    public int getId_categoria() {
        return id_categoria;
    }
    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }
    public int getStock_actual() {
        return stock_actual;
    }
    public void setStock_actual(int stock_actual) {
        this.stock_actual = stock_actual;
    }
    public int getStock_minimo() {
        return stock_minimo;
    }
    public void setStock_minimo(int stock_minimo) {
        this.stock_minimo = stock_minimo;
    }
    public String getCodigo_producto() {
        return codigo_producto;
    }
    public void setCodigo_producto(String codigo_producto) {
        this.codigo_producto = codigo_producto;
    }
    public String getNombre_producto() {
        return nombre_producto;
    }
    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public float getPrecio_compra() {
        return precio_compra;
    }
    public void setPrecio_compra(float precio_compra) {
        this.precio_compra = precio_compra;
    }
    public float getPrecio_venta() {
        return precio_venta;
    }
    public void setPrecio_venta(float precio_venta) {
        this.precio_venta = precio_venta;
    }
    public Date getFecha_vencimiento() {
        return fecha_vencimiento;
    }
    public void setFecha_vencimiento(Date fecha_vencimiento) {
        this.fecha_vencimiento = fecha_vencimiento;
    }
    public Date getFecha_creacion() {
        return fecha_creacion;
    }
    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
    public boolean isActivo() {
        return activo;
    }
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    public CategoriaProducto getCategoria() {
        return categoria;
    }
    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

  public void setCodigo(String codigo) {
    this.codigo_producto = codigo;
}

public String getCodigo() {
    return codigo_producto;
}
}
