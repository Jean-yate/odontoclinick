package beans;

import dao.CategoriaProductoDAO;
import dao.ProductoDAO;
import modelo.CategoriaProducto;
import modelo.Producto;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@ViewScoped
public class ProductoBean implements Serializable {

    // Objetos principales
    private Producto producto;
    private List<Producto> listaProductos;
    
    // Para el combo box de categorías en el formulario
    private List<CategoriaProducto> listaCategorias; 
    private int idCategoriaSeleccionada; 

    // DAOs
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaProductoDAO categoriaDAO = new CategoriaProductoDAO();

    @PostConstruct
    public void init() {
        this.producto = new Producto();
        this.listar();
        this.listarCategorias();
    }

    // --- MÉTODOS EXISTENTES (NO TOCADOS) ---

    public void listar() {
        try {
            listaProductos = productoDAO.listar();
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }
    
    public void listarCategorias() {
        try {
            listaCategorias = categoriaDAO.listar(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrar() {
        
        System.out.println("Fecha vencimiento: " + producto.getFecha_vencimiento());
System.out.println("Fecha creación: " + producto.getFecha_creacion());

    try {
        CategoriaProducto cat = new CategoriaProducto();
        cat.setId_categoria(idCategoriaSeleccionada);
        producto.setCategoria(cat);

        // 🔹 FECHA AUTOMÁTICA
        if (producto.getFecha_creacion() == null) {
            producto.setFecha_creacion(new java.util.Date());
        }

        if (producto.getFecha_vencimiento() == null) {
            producto.setFecha_vencimiento(new java.util.Date());
        }

        if (producto.getId_producto() > 0) {
            productoDAO.actualizar(producto);
            mensaje("Éxito", "Producto actualizado correctamente.");
        } else {
            productoDAO.guardar(producto);
            mensaje("Éxito", "Producto registrado correctamente.");
        }

        limpiar();
        listar();
    } catch (Exception e) {
        e.printStackTrace();
        mensajeError("Error", "Error al guardar el producto");
    }
}


    public void leer(Producto prodSeleccionado) {
        this.producto = prodSeleccionado;
        if (this.producto.getCategoria() != null) {
            this.idCategoriaSeleccionada = this.producto.getCategoria().getId_categoria();
        }
    }

    public void eliminar(Producto prodSeleccionado) { // Este recibe objeto
        try {
            productoDAO.eliminar(prodSeleccionado.getId_producto());
            listar(); 
            mensaje("Info", "Producto eliminado.");
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }
    
    // Sobrecarga para eliminar por ID (si la vista manda solo el int)
    public void eliminar(int id) {
        try {
            productoDAO.eliminar(id);
            listar();
            mensaje("Info", "Producto eliminado.");
        } catch (Exception e) {
            System.out.println("Error al eliminar por ID: " + e.getMessage());
        }
    }

    public void limpiar() {
        this.producto = new Producto();
        this.idCategoriaSeleccionada = 0;
    }

    // =======================================================
    // NUEVOS MÉTODOS (PUENTES PARA LA VISTA)
    // =======================================================

    // La vista llama a 'prepararNuevo', nosotros llamamos a 'limpiar'
    public void prepararNuevo() {
        this.limpiar();
    }

    // La vista llama a 'prepararEdicion', nosotros llamamos a 'leer'
    public void prepararEdicion(Producto p) {
        this.leer(p);
    }

    // La vista llama a 'guardar', nosotros llamamos a 'registrar'
    public void guardar() {
        this.registrar();
    }

    // La vista llama a 'productoActual', nosotros devolvemos 'producto'
    public Producto getProductoActual() {
        return this.producto;
    }

    // Lógica visual para el semáforo de colores en la tabla
    public boolean esStockBajo(int stock) {
        return stock <= 5; 
    }

    // Helpers para mensajes
    private void mensaje(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, t, m)); }
    private void mensajeError(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t, m)); }

    // --- GETTERS Y SETTERS ---

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public List<Producto> getListaProductos() { return listaProductos; }
    public void setListaProductos(List<Producto> listaProductos) { this.listaProductos = listaProductos; }

    public List<CategoriaProducto> getListaCategorias() { return listaCategorias; }
    public void setListaCategorias(List<CategoriaProducto> listaCategorias) { this.listaCategorias = listaCategorias; }

    public int getIdCategoriaSeleccionada() { return idCategoriaSeleccionada; }
    public void setIdCategoriaSeleccionada(int idCategoriaSeleccionada) { this.idCategoriaSeleccionada = idCategoriaSeleccionada; }
}