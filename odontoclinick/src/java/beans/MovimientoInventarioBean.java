package beans;

import dao.MovimientoInventarioDAO;
import dao.ProductoDAO; // Necesario para listar los productos en el combo
import modelo.MovimientoInventario;
import modelo.Producto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class MovimientoInventarioBean implements Serializable {

    // Objetos principales
    private MovimientoInventario movimiento;
    private List<MovimientoInventario> listaMovimientos;
    
    // Listas y variables para relacionar con Producto (Combo Box)
    private List<Producto> listaProductos;
    private int idProductoSeleccionado;

    // Variables para Usuario (asumiendo que hay un sistema de usuarios)
    // En un sistema real, esto vendría de la sesión del login
    private int idUsuarioSeleccionado; 

    // DAOs
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    @PostConstruct
    public void init() {
        this.movimiento = new MovimientoInventario();
        this.listar();
        this.listarProductos(); // Cargar productos para el dropdown
        
        // Valor por defecto temporal para usuario (borrar si tienes login implementado)
        this.idUsuarioSeleccionado = 1; 
    }

    // --- MÉTODOS DE NEGOCIO ---

    public void listar() {
        try {
            listaMovimientos = movimientoDAO.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarProductos() {
        try {
            listaProductos = productoDAO.listar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrar() {
        try {
            // 1. Asignar las claves foráneas seleccionadas
            movimiento.setIdProducto(idProductoSeleccionado);
            movimiento.setIdUsuario(idUsuarioSeleccionado);
            
            // 2. Asignar fecha actual automática si es un nuevo registro
            if (movimiento.getFechaMovimiento() == null) {
                Date date = new Date();
                movimiento.setFechaMovimiento(new Timestamp(date.getTime()));
            }

            /* NOTA LÓGICA IMPORTANTE:
               Aquí deberías implementar la lógica para calcular 'stockAnterior' y 'stockNuevo'
               automáticamente basándote en el producto seleccionado, en lugar de escribirlos manualmente.
               
               Ejemplo pseudo-código:
               Producto prod = productoDAO.buscar(idProductoSeleccionado);
               movimiento.setStockAnterior(prod.getStock_actual());
               if(movimiento.getTipoMovimiento().equals("ENTRADA")) {
                   movimiento.setStockNuevo(prod.getStock_actual() + movimiento.getCantidad());
               } ...
            */

            // 3. Guardar o Actualizar
            if (movimiento.getIdMovimiento() > 0) {
                movimientoDAO.actualizar(movimiento);
            } else {
                movimientoDAO.guardar(movimiento);
            }

            limpiar();
            listar();
        } catch (Exception e) {
            System.out.println("Error al registrar movimiento: " + e.getMessage());
        }
    }

    public void leer(MovimientoInventario movSeleccionado) {
        this.movimiento = movSeleccionado;
        // Sincronizar el combo box de producto
        this.idProductoSeleccionado = this.movimiento.getIdProducto();
        this.idUsuarioSeleccionado = this.movimiento.getIdUsuario();
    }

    public void eliminar(MovimientoInventario movSeleccionado) {
        try {
            movimientoDAO.eliminar(movSeleccionado.getIdMovimiento());
            listar();
        } catch (Exception e) {
            System.out.println("Error al eliminar movimiento: " + e.getMessage());
        }
    }

    public void limpiar() {
        this.movimiento = new MovimientoInventario();
        this.idProductoSeleccionado = 0;
        // idUsuarioSeleccionado se mantiene si es el usuario logueado
    }

    // --- GETTERS Y SETTERS ---

    public MovimientoInventario getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(MovimientoInventario movimiento) {
        this.movimiento = movimiento;
    }

    public List<MovimientoInventario> getListaMovimientos() {
        return listaMovimientos;
    }

    public void setListaMovimientos(List<MovimientoInventario> listaMovimientos) {
        this.listaMovimientos = listaMovimientos;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public int getIdProductoSeleccionado() {
        return idProductoSeleccionado;
    }

    public void setIdProductoSeleccionado(int idProductoSeleccionado) {
        this.idProductoSeleccionado = idProductoSeleccionado;
    }

    public int getIdUsuarioSeleccionado() {
        return idUsuarioSeleccionado;
    }

    public void setIdUsuarioSeleccionado(int idUsuarioSeleccionado) {
        this.idUsuarioSeleccionado = idUsuarioSeleccionado;
    }
}