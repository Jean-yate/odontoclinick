package beans;

import dao.UsuarioDAO;
import modelo.Usuario;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "adminBean")
@ViewScoped
public class AdminBean implements Serializable {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private List<Usuario> listaAdmins;
    private Usuario adminActual;

    @PostConstruct
    public void init() {
        prepararNuevo();
        cargarLista();
    }

    public void prepararNuevo() {
        adminActual = new Usuario();
        adminActual.setId_rol(1); // 1 = Admin
        adminActual.setId_estado(1); // 1 = Activo
    }

    public void cargarLista() {
        // Necesitamos agregar este método a UsuarioDAO, ese SÍ falta.
        listaAdmins = usuarioDAO.listarPorRol(1); 
    }

    public void guardarAdmin() {
        try {
            // Validaciones básicas aquí si quieres...

            if (adminActual.getId_usuario() == 0) {
                // CREAR: El DAO es void, así que solo lo llamamos
                usuarioDAO.guardar(adminActual);
                mensaje("Éxito", "Administrador creado correctamente.");
            } else {
                // EDITAR: El DAO es void
                usuarioDAO.actualizar(adminActual);
                mensaje("Éxito", "Administrador actualizado.");
            }

            // Si llegamos aquí sin ir al catch, todo salió bien
            cargarLista();
            prepararNuevo();

        } catch (Exception e) {
            // Si el DAO falla (ej: SQL exception), caerá aquí
            mensajeError("Error", "No se pudo realizar la operación: " + e.getMessage());
        }
    }
    
    public void eliminarAdmin(Usuario u) {
        try {
            u.setId_estado(2); // Inactivo
            usuarioDAO.actualizar(u); // Void call
            cargarLista();
            mensaje("Info", "Administrador desactivado.");
        } catch (Exception e) {
            mensajeError("Error", "No se pudo eliminar.");
        }
    }

    // Helpers y Getters/Setters
    private void mensaje(String titulo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, texto));
    }
    private void mensajeError(String titulo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, titulo, texto));
    }

    public List<Usuario> getListaAdmins() { return listaAdmins; }
    public void setListaAdmins(List<Usuario> listaAdmins) { this.listaAdmins = listaAdmins; }
    public Usuario getAdminActual() { return adminActual; }
    public void setAdminActual(Usuario adminActual) { this.adminActual = adminActual; }
}