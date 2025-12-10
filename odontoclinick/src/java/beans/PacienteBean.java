package beans;

import dao.PacienteDAO;
import modelo.Paciente;
import modelo.Usuario;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "pacienteBean")
@ViewScoped
public class PacienteBean implements Serializable {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private List<Paciente> listaPacientes;
    
    private Paciente pacienteActual;
    private Usuario usuarioActual;
    private String textoBusqueda; // Para el filtro de búsqueda

    @PostConstruct
    public void init() {
        prepararNuevo();
        buscar();
    }

    public void prepararNuevo() {
        pacienteActual = new Paciente();
        usuarioActual = new Usuario();
        usuarioActual.setId_rol(4); // 4 = Paciente
    }

    public void buscar() {
        // Usamos el método con filtro que creamos en el paso anterior
        listaPacientes = pacienteDAO.listarConFiltro(textoBusqueda);
    }

    public void guardarPaciente() {
        try {
            // Validaciones
            if (usuarioActual.getNombre() == null || usuarioActual.getNombre().isEmpty()) {
                mensajeError("Error", "El nombre es obligatorio");
                return;
            }

            boolean exito = pacienteDAO.registrarPacienteTransaccion(usuarioActual, pacienteActual);

            if (exito) {
                mensaje("Éxito", "Paciente registrado con historial e usuario.");
                buscar();
                prepararNuevo();
            } else {
                mensajeError("Error", "Fallo al registrar paciente.");
            }
        } catch (Exception e) {
            mensajeError("Error", e.getMessage());
        }
    }

    // Helpers y Getters/Setters iguales...
    private void mensaje(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, t, m)); }
    private void mensajeError(String t, String m) { FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, t, m)); }
    
    public List<Paciente> getListaPacientes() { return listaPacientes; }
    public void setListaPacientes(List<Paciente> l) { this.listaPacientes = l; }
    public Paciente getPacienteActual() { return pacienteActual; }
    public void setPacienteActual(Paciente p) { this.pacienteActual = p; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario u) { this.usuarioActual = u; }
    public String getTextoBusqueda() { return textoBusqueda; }
    public void setTextoBusqueda(String t) { this.textoBusqueda = t; }
}