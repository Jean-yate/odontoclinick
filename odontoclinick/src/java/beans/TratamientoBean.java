package controlador;

import dao.TratamientoDAO;
import modelo.Tratamiento;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class TratamientoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Dependencia del DAO
    private TratamientoDAO tratamientoDAO = new TratamientoDAO();

    // Variables de Vista
    private List<Tratamiento> listaTratamientos;
    private Tratamiento tratamientoActual;

    // --- INICIALIZACIÓN ---
    @PostConstruct
    public void init() {
        // Cargar la lista al entrar a la página
        cargarLista();
        // Inicializar objeto vacío para el formulario
        tratamientoActual = new Tratamiento();
        tratamientoActual.setActivo(true); // Por defecto activo al crear
    }

    public void cargarLista() {
        listaTratamientos = tratamientoDAO.listarTodos();
    }

    // --- ACCIONES (Equivalente a métodos del Controller de Laravel) ---

    // 1. Preparar formulario para crear (Reset)
    public void prepararNuevo() {
        tratamientoActual = new Tratamiento();
        tratamientoActual.setActivo(true);
    }

    // 2. Preparar formulario para editar (Cargar datos)
    public void prepararEdicion(Tratamiento t) {
        // Clonamos o asignamos la referencia.
        // Asignar referencia directa funciona bien en ViewScoped
        this.tratamientoActual = t; 
    }

    // 3. Guardar (Store/Update combinado)
    public void guardar() {
        try {
            if (tratamientoActual.getId_tratamiento() == 0) {
                // Es un NUEVO registro (INSERT)
                tratamientoDAO.guardar(tratamientoActual);
                mensaje("Éxito", "Tratamiento creado correctamente.");
            } else {
                // Es una ACTUALIZACIÓN (UPDATE)
                tratamientoDAO.actualizar(tratamientoActual);
                mensaje("Éxito", "Tratamiento actualizado correctamente.");
            }

            // Recargar tabla y limpiar formulario
            cargarLista();
            prepararNuevo();

        } catch (Exception e) {
            e.printStackTrace();
            mensajeError("Error al guardar: " + e.getMessage());
        }
    }

    // 4. Eliminar (Destroy)
    public void eliminar(Tratamiento t) {
        try {
            tratamientoDAO.eliminar(t.getId_tratamiento());
            mensaje("Aviso", "Tratamiento eliminado correctamente.");
            cargarLista();
        } catch (Exception e) {
            e.printStackTrace();
            mensajeError("No se pudo eliminar (verifique que no tenga citas asociadas).");
        }
    }

    // --- UTILITARIOS ---

    private void mensaje(String titulo, String contenido) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_INFO, titulo, contenido));
    }

    private void mensajeError(String contenido) {
        FacesContext.getCurrentInstance().addMessage(null, 
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", contenido));
    }

    // --- GETTERS Y SETTERS ---

    public List<Tratamiento> getListaTratamientos() {
        return listaTratamientos;
    }

    public void setListaTratamientos(List<Tratamiento> listaTratamientos) {
        this.listaTratamientos = listaTratamientos;
    }

    public Tratamiento getTratamientoActual() {
        return tratamientoActual;
    }

    public void setTratamientoActual(Tratamiento tratamientoActual) {
        this.tratamientoActual = tratamientoActual;
    }
}