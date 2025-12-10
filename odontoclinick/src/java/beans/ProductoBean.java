package beans;

import dao.CategoriaProductoDAO;
import dao.ProductoDAO;
import modelo.CategoriaProducto;
import modelo.Producto;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ProductoBean implements Serializable {

    // Objetos principales
    private Producto producto;
    private List<Producto> listaProductos;
    
    // Para el combo box de categorías en el formulario
    private List<CategoriaProducto> listaCategorias; 
    private int idCategoriaSeleccionada; // Para capturar la selección del combo

    // DAOs
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final CategoriaProductoDAO categoriaDAO = new CategoriaProductoDAO();

    @PostConstruct
    public void init() {
        // Inicializamos el objeto y cargamos las listas al entrar a la página
        this.producto = new Producto();
        this.listar();
        this.listarCategorias();
    }

    // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

    public void listar() {
        try {
            listaProductos = productoDAO.listar();
        } catch (Exception e) {
            e.printStackTrace(); // Considera usar un logger o FacesMessage
        }
    }
    
    public void listarCategorias() {
        try {
            // Asumo que tu CategoriaDAO tiene un método listar() similar
            listaCategorias = categoriaDAO.listar(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registrar() {
        try {
            // Asignamos la categoría seleccionada al producto
            CategoriaProducto cat = new CategoriaProducto();
            cat.setId_categoria(idCategoriaSeleccionada);
            producto.setCategoria(cat);
            
            // Lógica para saber si guardar o actualizar (dependiendo si tiene ID)
            if (producto.getId_producto() > 0) {
                productoDAO.actualizar(producto);
            } else {
                productoDAO.guardar(producto);
            }
            
            limpiar();
            listar(); // Actualizar la tabla
        } catch (Exception e) {
            System.out.println("Error al registrar: " + e.getMessage());
        }
    }

    public void leer(Producto prodSeleccionado) {
        // Copiamos el producto seleccionado al objeto del formulario para editarlo
        this.producto = prodSeleccionado;
        // Sincronizamos el combo box de categoría
        if (this.producto.getCategoria() != null) {
            this.idCategoriaSeleccionada = this.producto.getCategoria().getId_categoria();
        }
    }

    public void eliminar(Producto prodSeleccionado) {
        try {
            productoDAO.eliminar(prodSeleccionado.getId_producto());
            listar(); // Refrescar la tabla
        } catch (Exception e) {
            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }

    public void limpiar() {
        this.producto = new Producto();
        this.idCategoriaSeleccionada = 0;
    }

    // --- GETTERS Y SETTERS ---

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public List<Producto> getListaProductos() {
        return listaProductos;
    }

    public void setListaProductos(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    public List<CategoriaProducto> getListaCategorias() {
        return listaCategorias;
    }

    public void setListaCategorias(List<CategoriaProducto> listaCategorias) {
        this.listaCategorias = listaCategorias;
    }

    public int getIdCategoriaSeleccionada() {
        return idCategoriaSeleccionada;
    }

    public void setIdCategoriaSeleccionada(int idCategoriaSeleccionada) {
        this.idCategoriaSeleccionada = idCategoriaSeleccionada;
    }
}